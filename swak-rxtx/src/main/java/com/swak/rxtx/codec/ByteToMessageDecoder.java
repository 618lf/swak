package com.swak.rxtx.codec;

import static java.lang.Integer.MAX_VALUE;

import java.util.List;

import com.swak.rxtx.channel.Channel;
import com.swak.rxtx.channel.ChannelHandler;
import com.swak.utils.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;

/**
 * 将 byte[] 专为 Object
 * 
 * @author lifeng
 */
public abstract class ByteToMessageDecoder extends ChannelHandler {

	ByteBuf cumulation;
	private boolean first;
	private Cumulator cumulator = MERGE_CUMULATOR.INSTANCE;

	@Override
	public void read(Channel channel, Object data) {
		if (data instanceof ByteBuf) {
			List<Object> out = Lists.newArrayList();
			try {
				ByteBuf buf = (ByteBuf) data;
				first = cumulation == null;
				if (first) {
					cumulation = buf;
				} else {
					cumulation = cumulator.cumulate(channel.alloc(), cumulation, buf);
				}
				this.callDecode(channel, cumulation, out);
			} catch (Exception e) {
				throw e;
			} finally {
				if (cumulation != null && !cumulation.isReadable()) {
					cumulation.release();
					cumulation = null;
				}
				// 触发已有数据的处理
				this.fireChannelRead(channel, out);
			}
		} else {
			super.read(channel, data);
		}
	}

	/**
	 * 关闭通道时
	 */
	@Override
	public void close(Channel channel) {
		List<Object> out = Lists.newArrayList();
		try {
			this.callDecode(channel, cumulation != null ? cumulation : Unpooled.EMPTY_BUFFER, out);
		} catch (DecoderException e) {
			throw e;
		} catch (Exception e) {
			throw new DecoderException(e);
		} finally {
			if (cumulation != null) {
				cumulation.release();
				cumulation = null;
			}

			// 剩余数据的处理
			this.fireChannelRead(channel, out);

			// 触发关闭事件的继续处理
			super.close(channel);
		}
	}

	/**
	 * 调用解码
	 * 
	 * @param channel
	 * @param in
	 * @param out
	 */
	protected void callDecode(Channel channel, ByteBuf in, List<Object> out) {
		try {
			while (in.isReadable()) {
				int outSize = out.size();

				// 触发已有数据的处理
				if (outSize > 0) {
					this.fireChannelRead(channel, out);
					out.clear();
					outSize = 0;
				}

				// 一致解码直到没有需要解码的数据
				int oldInputLength = in.readableBytes();
				this.decode(channel, in, out);

				// 可能数据不够不需要解码
				if (outSize == out.size()) {
					if (oldInputLength == in.readableBytes()) {
						break;
					} else {
						continue;
					}
				}
			}
		} catch (DecoderException e) {
			throw e;
		} catch (Exception cause) {
			throw new DecoderException(cause);
		}
	}

	/**
	 * 子类实现解码
	 * 
	 * @param channel
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	protected abstract void decode(Channel channel, ByteBuf in, List<Object> out) throws Exception;

	/**
	 * 触发后续处理
	 * 
	 * @param channel
	 * @param msgs
	 */
	void fireChannelRead(Channel channel, List<Object> msgs) {
		for (int i = 0; i < msgs.size(); i++) {
			super.read(channel, msgs.get(i));
		}
	}

	/**
	 * 唯一合并器
	 * 
	 * @author lifeng
	 *
	 */
	enum MERGE_CUMULATOR implements Cumulator {
		INSTANCE
	}

	/**
	 * 合并器
	 * 
	 * @author lifeng
	 */
	interface Cumulator {

		/**
		 * 合并
		 * 
		 * @param cumulation
		 * @param in
		 * @return
		 */
		default ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
			try {
				final int required = in.readableBytes();
				if (required > cumulation.maxWritableBytes()
						|| (required > cumulation.maxFastWritableBytes() && cumulation.refCnt() > 1)
						|| cumulation.isReadOnly()) {
					return expandCumulation(alloc, cumulation, in);
				}
				return cumulation.writeBytes(in);
			} finally {
				in.release();
			}
		}

		/**
		 * 合并
		 * 
		 * @param alloc
		 * @param oldCumulation
		 * @param in
		 * @return
		 */
		default ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf oldCumulation, ByteBuf in) {
			ByteBuf newCumulation = alloc
					.buffer(alloc.calculateNewCapacity(oldCumulation.readableBytes() + in.readableBytes(), MAX_VALUE));
			ByteBuf toRelease = newCumulation;
			try {
				newCumulation.writeBytes(oldCumulation);
				newCumulation.writeBytes(in);
				toRelease = oldCumulation;
				return newCumulation;
			} finally {
				toRelease.release();
			}
		}
	}
}
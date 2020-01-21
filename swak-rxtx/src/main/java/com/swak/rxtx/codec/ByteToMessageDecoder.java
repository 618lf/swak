package com.swak.rxtx.codec;

import static java.lang.Integer.MAX_VALUE;

import java.util.List;

import com.swak.rxtx.channel.Channel;
import com.swak.rxtx.channel.ChannelHandler;
import com.swak.utils.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
			} finally {
				if (cumulation != null && !cumulation.isReadable()) {
					cumulation.release();
					cumulation = null;
				}
			}
		} else {
			super.read(channel, data);
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
				
				// 子类解码
				this.decode(channel, in, out);
				break;
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

					ByteBuf newCumulation = alloc.buffer(
							alloc.calculateNewCapacity(cumulation.readableBytes() + in.readableBytes(), MAX_VALUE));
					newCumulation.writeBytes(cumulation);
					newCumulation.writeBytes(in);
					return newCumulation;
				}
				return cumulation.writeBytes(in);
			} finally {
				in.release();
				cumulation.release();
			}
		}
	}
}
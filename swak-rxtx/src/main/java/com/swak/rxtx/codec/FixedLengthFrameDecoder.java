package com.swak.rxtx.codec;

import static io.netty.util.internal.ObjectUtil.checkPositive;

import java.util.List;

import com.swak.rxtx.channel.Channel;

import io.netty.buffer.ByteBuf;

/**
 * 固定长度的解码
 * 
 * @author lifeng
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

	private final int frameLength;

	public FixedLengthFrameDecoder(int frameLength) {
		checkPositive(frameLength, "frameLength");
		this.frameLength = frameLength;
	}

	@Override
	protected void decode(Channel channel, ByteBuf in, List<Object> out) throws Exception {
		Object decoded = decode(channel, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	/**
	 * Create a frame out of the {@link ByteBuf} and return it.
	 */
	protected Object decode(Channel channel, ByteBuf in) throws Exception {
		if (in.readableBytes() < frameLength) {
			return null;
		} else {
			return in.readRetainedSlice(frameLength);
		}
	}
}
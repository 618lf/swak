package com.swak.common.cache.redis.factory;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.ToByteBufEncoder;
import io.lettuce.core.protocol.LettuceCharsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * key utf-8 String value byte[]
 * 
 * @author lifeng
 */
public class StringByteArrayCodec implements RedisCodec<String, byte[]>, ToByteBufEncoder<String, byte[]> {

	private static final byte[] EMPTY = new byte[0];

	@Override
	public void encodeKey(String key, ByteBuf target) {
		ByteBufUtil.writeUtf8(target, key);
	}

	@Override
	public void encodeValue(byte[] value, ByteBuf target) {
		target.writeBytes(value);
	}

	@Override
	public int estimateSize(Object keyOrValue) {
		if (keyOrValue == null) {
			return 0;
		}
		if (keyOrValue instanceof String) {
			CharsetEncoder encoder = CharsetUtil.encoder(LettuceCharsets.UTF8);
			return (int) (encoder.averageBytesPerChar() * ((String) keyOrValue).length());
		}
		return ((byte[]) keyOrValue).length;
	}

	@Override
	public String decodeKey(ByteBuffer bytes) {
		return Unpooled.wrappedBuffer(bytes).toString(LettuceCharsets.UTF8);
	}

	@Override
	public byte[] decodeValue(ByteBuffer bytes) {
		int remaining = bytes.remaining();

		if (remaining == 0) {
			return EMPTY;
		}

		byte[] b = new byte[remaining];
		bytes.get(b);
		return b;
	}

	@Override
	public ByteBuffer encodeKey(String key) {
		if (key == null) {
			return ByteBuffer.wrap(EMPTY);
		}
		CharsetEncoder encoder = CharsetUtil.encoder(LettuceCharsets.UTF8);
		ByteBuffer buffer = ByteBuffer.allocate((int) (encoder.maxBytesPerChar() * key.length()));
		ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
		byteBuf.clear();
		ByteBufUtil.writeUtf8(byteBuf, key);
		buffer.limit(byteBuf.writerIndex());
		return buffer;
	}

	@Override
	public ByteBuffer encodeValue(byte[] value) {
		if (value == null) {
			return ByteBuffer.wrap(EMPTY);
		}

		return ByteBuffer.wrap(value);
	}
}

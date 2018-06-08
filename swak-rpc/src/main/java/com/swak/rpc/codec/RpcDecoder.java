package com.swak.rpc.codec;

import java.util.List;

import com.swak.serializer.SerializationUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码数据
 * @author lifeng
 */
public class RpcDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		 if (in.readableBytes() < 4) {
	            return;
	        }
	        in.markReaderIndex();
	        int dataLength = in.readInt();
	        if (dataLength < 0) {
	            ctx.close();
	        }
	        if (in.readableBytes() < dataLength) {
	            in.resetReaderIndex();
	            return;	// fix 1024k buffer splice limix
	        }
	        byte[] data = new byte[dataLength];
	        in.readBytes(data);

	        Object obj = SerializationUtils.deserialize(data);
	        out.add(obj);
	}
}
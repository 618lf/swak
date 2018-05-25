package com.swak.reactivex.server;

/**
 * Constant for names used when adding/removing {@link io.netty.channel.ChannelHandler}.
 *
 * Order of placement :
 * <p>
 * {@code
 * -> proxy ? [ProxyHandler]
 * -> ssl ? [SslHandler]
 * -> ssl & trace log ? [SslLoggingHandler]
 * -> ssl ? [SslReader]
 * -> log ? [LoggingHandler]
 * -> http ? [HttpCodecHandler]
 * -> http ws ? [HttpAggregator]
 * -> http server  ? [HttpServerHandler]
 * -> onWriteIdle ? [OnChannelWriteIdle]
 * -> onReadIdle ? [OnChannelReadIdle]
 * -> http form/multipart ? [ChunkedWriter]
 * => [ReactiveBridge]
 * }
 *
 * @author Stephane Maldini
 * @since 0.6
 */
public interface NettyPipeline {

	String LEFT = "reactor.left.";
	String RIGHT = "reactor.right.";

	String SslHandler         = LEFT + "sslHandler";
	String SslReader          = LEFT + "sslReader";
	String SslLoggingHandler  = LEFT + "sslLoggingHandler";
	String ProxyHandler       = LEFT + "proxyHandler";
	String ReactiveBridge     = RIGHT + "reactiveBridge";
	String HttpCodec          = LEFT + "httpCodec";
	String HttpDecompressor   = LEFT + "decompressor";
	String HttpCompressor     = LEFT + "compressor";
	String HttpAggregator     = LEFT + "httpAggregator";
	String HttpServerHandler  = LEFT + "httpServerHandler";
	String OnChannelWriteIdle = LEFT + "onChannelWriteIdle";
	String OnChannelReadIdle  = LEFT + "onChannelReadIdle";
	String ChunkedWriter      = LEFT + "chunkedWriter";
	String LoggingHandler     = LEFT + "loggingHandler";
	String CompressionHandler = LEFT + "compressionHandler";
}

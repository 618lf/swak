package com.swak.reactivex.server;

/**
 * @author lifeng
 */
public enum TransportMode {

	/**
     * Use NIO transport.
     */
    NIO,

    /**
     * Use EPOLL transport. Activates an unix socket if servers binded to loopback interface.
     * Requires <b>netty-transport-native-epoll</b> lib in classpath.
     */
    EPOLL
}

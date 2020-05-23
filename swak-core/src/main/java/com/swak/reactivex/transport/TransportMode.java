package com.swak.reactivex.transport;

/**
 * TransportMode
 *
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
    EPOLL,
    
    /**
     * OS Adaptation, Linux us EPOLL, Others use NIO
     */
    OS
}
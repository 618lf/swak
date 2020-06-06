package com.swak.fetty.transport;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.swak.fetty.transport.eventloop.EventLoopGroup;

/**
 * 服务启动
 * 
 * @author lifeng
 * @date 2020年5月26日 下午1:04:38
 */
public class ServerBootStarter extends AbstractBootStarper {

	EventLoopGroup childGroup;

	public AbstractBootStarper group(EventLoopGroup parent, EventLoopGroup childGroup) {
		assert parent != null;
		this.group = parent;
		this.childGroup = childGroup;
		return this;
	}

	/**
	 * 绑定到此端口
	 * 
	 * @param port
	 * @throws Exception
	 */
	public void bind(int port) throws Exception {
		// 1、创建网络服务端 ServerSocketChannel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		// 2、构建一个Selector选择器，将channel注册上去
		Selector selector = Selector.open();
		// 将serverSocketChannel注册到selector
		SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
		selectionKey.interestOps(SelectionKey.OP_ACCEPT);

		// 3、绑定端口并启动
		serverSocketChannel.bind(new InetSocketAddress(port));
		System.out.println("服务器启动");
		while (true) {
			// select 方法具有阻塞效果，直到有事件通知才会返回
			System.out.println("等待客户端连接...");
			selector.select();
			// 获取事件
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			// 遍历查询结果
			Iterator<SelectionKey> iter = selectionKeys.iterator();
			while (iter.hasNext()) {
				SelectionKey key = iter.next();
				iter.remove();
				if (key.isAcceptable()) {
					System.out.println("Accepte Event");
				}
				if (key.isConnectable()) {
					System.out.println("Connecte Event");
				}
				if (key.isReadable()) {
					System.out.println("Read Event");
				}
				if (key.isWritable()) {
					System.out.println("Write Event");
				}
				if (key.isValid()) {
					System.out.println("Valid Event");
				}
				if (key.isAcceptable()) {
					this.selectorAcceptable(key, selector);
				}
				if (key.isReadable()) {
					this.selectorReadable(key);
				}
			}
			selector.selectNow();
		}
	}

	/**
	 * 监听到连接事件
	 */
	private void selectorAcceptable(SelectionKey key, Selector selector) throws Exception {
		ServerSocketChannel server = (ServerSocketChannel) key.attachment();
		SocketChannel clientSocketChannel = server.accept();
		clientSocketChannel.configureBlocking(false);
		clientSocketChannel.register(selector, SelectionKey.OP_READ, clientSocketChannel);
		System.out.println("收到新连接：" + clientSocketChannel.getRemoteAddress());
	}

	/**
	 * 监听到读事件
	 */
	private void selectorReadable(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.attachment();
			ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
			// 获取缓冲区数据
			this.readBufferFormChannel(socketChannel, requestBuffer);
			// 打印缓冲区的数据
			this.printBuffer(requestBuffer);
			// 返回响应消息
			this.responseMessageToChannel(socketChannel);
		} catch (Exception e) {
			// 取消事件订阅
			key.cancel();
		}
	}

	/**
	 * 没有读到数据，就会一直循环读，直到读到为止
	 */
	private void readBufferFormChannel(SocketChannel socketChannel, ByteBuffer requestBuffer) throws Exception {
		// 以read的方式将数据读取到缓冲区
		while (socketChannel.isOpen() && socketChannel.read(requestBuffer) != -1) {
			// 长连接情况下,需要手动判断数据有没有读取结束 (此处做一个简单的判断: 超过0字节就认为请求结束了)
			if (requestBuffer.position() > 0) {
				System.out.println("从 channel 中获取到了数据");
				break;
			}
		}
	}

	private void printBuffer(ByteBuffer requestBuffer) {
		// 开始读取数据
		requestBuffer.flip();
		byte[] content = new byte[requestBuffer.limit()];
		requestBuffer.get(content);
		System.out.println(String.format("%s 收到数据，内容为：%s", Thread.currentThread().getName(), new String(content)));
	}

	private void responseMessageToChannel(SocketChannel socketChannel) throws Exception {
		// 响应结果
		String response = "HTTP/1.1 200 OK\r\n" + "Content-Length: 11\r\n\r\n" + "Hello World";
		ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
		// 因为是非阻塞，所以要循环写，确保写入完整
		while (responseBuffer.hasRemaining()) {
			socketChannel.write(responseBuffer);
		}
		System.out.println(String.format("已响应回执，发送给 %s", socketChannel.getRemoteAddress()));
	}

	public static void main(String[] args) throws Exception {
		new ServerBootStarter().bind(8080);
	}
}
package com.swak.fetty.transport;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * NIO 的客户端
 */
public class NioClient {
	public static void main(String[] args) throws Exception {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
		// 没有链接上就要一直等待
		while (!socketChannel.finishConnect()) {
			Thread.yield();
		}
		Scanner scanner = new Scanner(System.in);
		System.out.println("请输入要发送给服务器的数据：");
		String msg = scanner.nextLine();
		ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
		// 收到服务器响应
		System.out.println("等待服务器响应：");
		ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
		readBufferFormChannel(socketChannel, requestBuffer);
		printBuffer(requestBuffer);
		scanner.close();
		socketChannel.close();
	}

	/**
	 * 没有读到数据，就会一直循环读，直到读到为止
	 */
	private static void readBufferFormChannel(SocketChannel socketChannel, ByteBuffer requestBuffer) throws Exception {
		// 以read的方式将数据读取到缓冲区
		while (socketChannel.isOpen() && socketChannel.read(requestBuffer) != -1) {
			// 长连接情况下,需要手动判断数据有没有读取结束 (此处做一个简单的判断: 超过0字节就认为请求结束了)
			if (requestBuffer.position() > 0) {
				System.out.println("从 channel 中获取到了数据");
				break;
			}
		}
	}

	private static void printBuffer(ByteBuffer requestBuffer) {
		// 开始读取数据
		requestBuffer.flip();
		byte[] content = new byte[requestBuffer.limit()];
		requestBuffer.get(content);
		System.out.println(String.format("%s 收到数据，内容为：%s", Thread.currentThread().getName(), new String(content)));
	}
}

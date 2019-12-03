package com.swak.rabbit.connection;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.swak.rabbit.message.PendingConfirm;

/**
 * 具有缓存的通道
 * 
 * @author lifeng
 */
public class CacheChannelProxy extends ChannelProxy implements ConfirmListener, ReturnListener, ShutdownListener {

	private final Logger logger = LoggerFactory.getLogger(CacheChannelProxy.class);
	private final TransferQueue<CacheChannelProxy> channels;
	private Listener listener;
	private SortedMap<Long, PendingConfirm> confirms;

	public CacheChannelProxy(TransferQueue<CacheChannelProxy> channels, Channel channel) {
		super(channel);
		this.channels = channels;
	}

	//////////////// 关闭 ///////////////
	@Override
	public void close() throws IOException, TimeoutException {
		this.logicClose();
	}

	@Override
	public void close(int arg0, String arg1) throws IOException, TimeoutException {
		this.logicClose();
	}

	/**
	 * 逻辑关闭,直接放入列表中
	 */
	public void logicClose() {
		if (delegate.isOpen()) {
			channels.add(this);
		}
	}

	/**
	 * 物理关闭，主动关闭条通道
	 */
	public void physicalClose() {
		try {
			delegate.close();
		} catch (IOException | TimeoutException e) {
		}
		generateNacksForPendingAcks("Channel closed by application");
	}

	//////////////// 添加待确认消息 /////////////////
	public void addPendingConfirm(long seq, PendingConfirm confirm) {
		confirms.put(seq, confirm);
	}

	/**
	 * 添加回调
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener) {
		delegate.addConfirmListener(this);
		delegate.addReturnListener(this);
		delegate.addShutdownListener(this);
		this.listener = listener;
		this.confirms = new ConcurrentSkipListMap<Long, PendingConfirm>();
	}

	/**
	 * 回调
	 * 
	 * @author lifeng
	 */
	public interface Listener {
		void handleConfirm(PendingConfirm pendingConfirm, boolean ack);

		void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
				AMQP.BasicProperties properties, byte[] body) throws IOException;
	}

	// 处理连接断开
	@Override
	public void shutdownCompleted(ShutdownSignalException cause) {
		if (logger.isDebugEnabled()) {
			logger.debug("Channel shutdown", cause);
		}
		generateNacksForPendingAcks(cause.getMessage());
	}

	// 主动关闭或者通道断开时处理未应答的消息
	private synchronized void generateNacksForPendingAcks(String cause) {
		for (Entry<Long, PendingConfirm> confirmEntry : confirms.entrySet()) {
			confirmEntry.getValue().setCause(cause);
			processAck(confirmEntry.getKey(), false, false);
		}
	}

	// 处理路由不可达发送时需设置 Mandatory = true
	@Override
	public void handleReturn(int arg0, String arg1, String arg2, String arg3, BasicProperties arg4, byte[] arg5)
			throws IOException {
		this.listener.handleReturn(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	// 处理消息确认
	@Override
	public void handleAck(long seq, boolean multiple) throws IOException {
		this.processAck(seq, multiple, true);
	}

	@Override
	public void handleNack(long seq, boolean multiple) throws IOException {
		this.processAck(seq, multiple, false);
	}

	private synchronized void processAck(long seq, boolean multiple, boolean ack) {
		if (multiple) {
			Map<Long, PendingConfirm> confirmsMap = confirms.headMap(seq + 1);
			Iterator<Entry<Long, PendingConfirm>> iterator = confirmsMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Long, PendingConfirm> entry = iterator.next();
				PendingConfirm pendingConfirm = entry.getValue();
				iterator.remove();
				listener.handleConfirm(pendingConfirm, ack);
			}
		} else {
			PendingConfirm pendingConfirm = confirms.remove(seq);
			listener.handleConfirm(pendingConfirm, ack);
		}
	}
}
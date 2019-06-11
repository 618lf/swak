package com.swak.rabbit.connection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.Basic.RecoverOk;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Confirm.SelectOk;
import com.rabbitmq.client.AMQP.Exchange.BindOk;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;
import com.rabbitmq.client.AMQP.Exchange.UnbindOk;
import com.rabbitmq.client.AMQP.Queue.PurgeOk;
import com.rabbitmq.client.AMQP.Tx.CommitOk;
import com.rabbitmq.client.AMQP.Tx.RollbackOk;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Command;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ReturnCallback;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * channel 的代理
 * 
 * @author lifeng
 */
public class ChannelProxy implements Channel {

	protected final Channel delegate;

	public ChannelProxy(Channel delegate) {
		this.delegate = delegate;
	}

	public Channel getDelegate() {
		return delegate;
	}

	@Override
	public void close() throws IOException, TimeoutException {
		delegate.close();
	}

	@Override
	public void close(int arg0, String arg1) throws IOException, TimeoutException {
		delegate.close(arg0, arg1);
	}

	@Override
	public void addShutdownListener(ShutdownListener arg0) {
		delegate.addShutdownListener(arg0);
	}

	@Override
	public ShutdownSignalException getCloseReason() {
		return delegate.getCloseReason();
	}

	@Override
	public boolean isOpen() {
		return delegate.isOpen();
	}

	@Override
	public void notifyListeners() {
		delegate.notifyListeners();
	}

	@Override
	public void removeShutdownListener(ShutdownListener arg0) {
		delegate.removeShutdownListener(arg0);
	}

	@Override
	public void abort() throws IOException {
		delegate.abort();
	}

	@Override
	public void abort(int arg0, String arg1) throws IOException {
		delegate.abort(arg0, arg1);
	}

	@Override
	public void addConfirmListener(ConfirmListener arg0) {
		delegate.addConfirmListener(arg0);
	}

	@Override
	public ConfirmListener addConfirmListener(ConfirmCallback arg0, ConfirmCallback arg1) {
		return delegate.addConfirmListener(arg0, arg1);
	}

	@Override
	public void addReturnListener(ReturnListener arg0) {
		delegate.addReturnListener(arg0);
	}

	@Override
	public ReturnListener addReturnListener(ReturnCallback arg0) {
		return delegate.addReturnListener(arg0);
	}

	@Override
	public CompletableFuture<Command> asyncCompletableRpc(Method arg0) throws IOException {
		return delegate.asyncCompletableRpc(arg0);
	}

	@Override
	public void asyncRpc(Method arg0) throws IOException {
		delegate.asyncRpc(arg0);
	}

	@Override
	public void basicAck(long arg0, boolean arg1) throws IOException {
		delegate.basicAck(arg0, arg1);
	}

	@Override
	public void basicCancel(String arg0) throws IOException {
		delegate.basicCancel(arg0);
	}

	@Override
	public String basicConsume(String arg0, Consumer arg1) throws IOException {
		return delegate.basicConsume(arg0, arg1);
	}

	@Override
	public String basicConsume(String arg0, DeliverCallback arg1, CancelCallback arg2) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2);
	}

	@Override
	public String basicConsume(String arg0, DeliverCallback arg1, ConsumerShutdownSignalCallback arg2)
			throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, Consumer arg2) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2);
	}

	@Override
	public String basicConsume(String arg0, DeliverCallback arg1, CancelCallback arg2,
			ConsumerShutdownSignalCallback arg3) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, DeliverCallback arg2, CancelCallback arg3)
			throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, DeliverCallback arg2, ConsumerShutdownSignalCallback arg3)
			throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, Map<String, Object> arg2, Consumer arg3) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, Consumer arg3) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, DeliverCallback arg2, CancelCallback arg3,
			ConsumerShutdownSignalCallback arg4) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, Map<String, Object> arg2, DeliverCallback arg3,
			CancelCallback arg4) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, Map<String, Object> arg2, DeliverCallback arg3,
			ConsumerShutdownSignalCallback arg4) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, DeliverCallback arg3, CancelCallback arg4)
			throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, DeliverCallback arg3,
			ConsumerShutdownSignalCallback arg4) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, Map<String, Object> arg2, DeliverCallback arg3,
			CancelCallback arg4, ConsumerShutdownSignalCallback arg5) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, DeliverCallback arg3, CancelCallback arg4,
			ConsumerShutdownSignalCallback arg5) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * @param queue
	 *            队列名
	 * @param autoAck
	 *            是否自动确认消息,true自动确认,false 不自动要手动调用,建立设置为false
	 * @param consumerTag
	 *            消费者标签，用来区分多个消费者
	 * @param noLocal
	 *            设置为true，表示 不能将同一个Conenction中生产者发送的消息传递给这个Connection中 的消费者
	 * @param exclusive
	 *            是否排他
	 * @param arguments
	 *            消费者的参数
	 * @param consumer
	 *            消费者 DefaultConsumer建立使用，重写其中的方法 handleShutdownSignal方法
	 *            当Channel与Conenction关闭的时候会调用， handleCancelOk方法会在其它方法之前调用，返回消费者标签
	 *            handleCancelOk与handleCancel消费者可以显式或者隐式的取水订单的时候调用，也可以通过
	 *            channel.basicCancel方法来显式的取消一个消费者订阅
	 *            会首先触发handleConsumeOk方法，之后触发handleDelivery方法，最后才触发handleCancelOk方法
	 */
	@Override
	public String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive,
			Map<String, Object> arguments, Consumer consumer) throws IOException {
		return delegate.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments, consumer);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5, DeliverCallback arg6, CancelCallback arg7) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5, DeliverCallback arg6, ConsumerShutdownSignalCallback arg7) throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}

	@Override
	public String basicConsume(String arg0, boolean arg1, String arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5, DeliverCallback arg6, CancelCallback arg7, ConsumerShutdownSignalCallback arg8)
			throws IOException {
		return delegate.basicConsume(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	}

	@Override
	public GetResponse basicGet(String arg0, boolean arg1) throws IOException {
		return delegate.basicGet(arg0, arg1);
	}

	@Override
	public void basicNack(long arg0, boolean arg1, boolean arg2) throws IOException {
		delegate.basicNack(arg0, arg1, arg2);
	}

	@Override
	public void basicPublish(String arg0, String arg1, BasicProperties arg2, byte[] arg3) throws IOException {
		delegate.basicPublish(arg0, arg1, arg2, arg3);
	}

	@Override
	public void basicPublish(String arg0, String arg1, boolean arg2, BasicProperties arg3, byte[] arg4)
			throws IOException {
		delegate.basicPublish(arg0, arg1, arg2, arg3, arg4);
	}

	/**
     * @param mandatory 当mandatory标志位设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的
     *        queue存储消息，那么broker会调用basic.return方法将消息返还给生产者;当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
     * @param immediate 不在使用
     * set. Note that the RabbitMQ server does not support this flag.
     */
	@Override
	public void basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate,
			BasicProperties props, byte[] body) throws IOException {
		delegate.basicPublish(exchange, routingKey, mandatory, immediate, props, body);
	}

	@Override
	public void basicQos(int arg0) throws IOException {
		delegate.basicQos(arg0);
	}

	@Override
	public void basicQos(int arg0, boolean arg1) throws IOException {
		delegate.basicQos(arg0, arg1);
	}

	@Override
	public void basicQos(int arg0, int arg1, boolean arg2) throws IOException {
		delegate.basicQos(arg0, arg1, arg2);
	}

	@Override
	public RecoverOk basicRecover() throws IOException {
		return delegate.basicRecover();
	}

	@Override
	public RecoverOk basicRecover(boolean arg0) throws IOException {
		return delegate.basicRecover(arg0);
	}

	@Override
	public void basicReject(long arg0, boolean arg1) throws IOException {
		delegate.basicReject(arg0, arg1);
	}

	@Override
	public void clearConfirmListeners() {
		delegate.clearConfirmListeners();
	}

	@Override
	public void clearReturnListeners() {
		delegate.clearReturnListeners();
	}

	@Override
	public SelectOk confirmSelect() throws IOException {
		return delegate.confirmSelect();
	}

	@Override
	public long consumerCount(String arg0) throws IOException {
		return delegate.consumerCount(arg0);
	}

	@Override
	public BindOk exchangeBind(String arg0, String arg1, String arg2) throws IOException {
		return delegate.exchangeBind(arg0, arg1, arg2);
	}

	@Override
	public BindOk exchangeBind(String arg0, String arg1, String arg2, Map<String, Object> arg3) throws IOException {
		return delegate.exchangeBind(arg0, arg1, arg2, arg3);
	}

	@Override
	public void exchangeBindNoWait(String arg0, String arg1, String arg2, Map<String, Object> arg3) throws IOException {
		delegate.exchangeBindNoWait(arg0, arg1, arg2, arg3);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, String arg1) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, BuiltinExchangeType arg1) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, String arg1, boolean arg2) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1, arg2);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, BuiltinExchangeType arg1, boolean arg2) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1, arg2);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, String arg1, boolean arg2, boolean arg3, Map<String, Object> arg4)
			throws IOException {
		return delegate.exchangeDeclare(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, BuiltinExchangeType arg1, boolean arg2, boolean arg3,
			Map<String, Object> arg4) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public DeclareOk exchangeDeclare(String arg0, String arg1, boolean arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5) throws IOException {
		return delegate.exchangeDeclare(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	 * @param exchange
	 *            交换器名称
	 * @param type
	 *            DIRECT("direct"), FANOUT("fanout"), TOPIC("topic"),
	 *            HEADERS("headers");
	 * @param durable
	 *            是否持久化,durable设置为true表示持久化,反之是非持久化，仅仅对队列持久化是没有意义的，需要对消息也进行持久化
	 *            BasicProperties(delivery_mode=2,)
	 * @param autoDelete
	 *            是否自动删除,设置为TRUE则表是自动删除,自删除的前提是至少有一个队列或者交换器与这交换器绑定,之后所有与这个交换器绑定的队列或者交换器都与此解绑,一般都设置为fase
	 * @param internal
	 *            是否内置,如果设置 为true,则表示是内置的交换器,客户端程序无法直接发送消息到这个交换器中,只能通过交换器路由到交换器的方式
	 * @param arguments
	 *            其它一些结构化参数比如
	 */
	@Override
	public DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete,
			boolean internal, Map<String, Object> arguments) throws IOException {
		return delegate.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments);
	}

	@Override
	public void exchangeDeclareNoWait(String arg0, String arg1, boolean arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5) throws IOException {
		delegate.exchangeDeclareNoWait(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void exchangeDeclareNoWait(String arg0, BuiltinExchangeType arg1, boolean arg2, boolean arg3, boolean arg4,
			Map<String, Object> arg5) throws IOException {
		delegate.exchangeDeclareNoWait(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public DeclareOk exchangeDeclarePassive(String arg0) throws IOException {
		return delegate.exchangeDeclarePassive(arg0);
	}

	@Override
	public DeleteOk exchangeDelete(String arg0) throws IOException {
		return delegate.exchangeDelete(arg0);
	}

	@Override
	public DeleteOk exchangeDelete(String arg0, boolean arg1) throws IOException {
		return delegate.exchangeDelete(arg0, arg1);
	}

	@Override
	public void exchangeDeleteNoWait(String arg0, boolean arg1) throws IOException {
		delegate.exchangeDeleteNoWait(arg0, arg1);
	}

	@Override
	public UnbindOk exchangeUnbind(String arg0, String arg1, String arg2) throws IOException {
		return delegate.exchangeUnbind(arg0, arg1, arg2);
	}

	@Override
	public UnbindOk exchangeUnbind(String arg0, String arg1, String arg2, Map<String, Object> arg3) throws IOException {
		return delegate.exchangeUnbind(arg0, arg1, arg2, arg3);
	}

	@Override
	public void exchangeUnbindNoWait(String arg0, String arg1, String arg2, Map<String, Object> arg3)
			throws IOException {
		delegate.exchangeUnbindNoWait(arg0, arg1, arg2, arg3);
	}

	@Override
	public int getChannelNumber() {
		return delegate.getChannelNumber();
	}

	@Override
	public Connection getConnection() {
		return delegate.getConnection();
	}

	@Override
	public Consumer getDefaultConsumer() {
		return delegate.getDefaultConsumer();
	}

	@Override
	public long getNextPublishSeqNo() {
		return delegate.getNextPublishSeqNo();
	}

	@Override
	public long messageCount(String arg0) throws IOException {
		return delegate.messageCount(arg0);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String arg0, String arg1, String arg2) throws IOException {
		return delegate.queueBind(arg0, arg1, arg2);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.BindOk queueBind(String arg0, String arg1, String arg2,
			Map<String, Object> arg3) throws IOException {
		return delegate.queueBind(arg0, arg1, arg2, arg3);
	}

	@Override
	public void queueBindNoWait(String arg0, String arg1, String arg2, Map<String, Object> arg3) throws IOException {
		delegate.queueBindNoWait(arg0, arg1, arg2, arg3);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare() throws IOException {
		return delegate.queueDeclare();
	}

	/**
	 * @param queue
	 *            队列名称
	 * @param durable
	 *            是不持久化， true ，表示持久化，会存盘，服务器重启仍然存在，false，非持久化
	 * @param exclusive
	 *            是否排他的，true，排他。如果一个队列声明为排他队列，该队列公对首次声明它的连接可见，并在连接断开时自动删除，
	 * @param autoDelete
	 *            是否自动删除,true，自动删除，自动删除的前提：至少有一个消息者连接到这个队列，之后所有与这个队列连接的消息都断开时，才会自动删除，备注：生产者客户端创建这个队列，或者没有消息者客户端连接这个队列时，不会自动删除这个队列
	 * @param arguments
	 *            其它一些参数
	 */
	@Override
	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive,
			boolean autoDelete, Map<String, Object> arguments) throws IOException {
		return delegate.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
	}

	@Override
	public void queueDeclareNoWait(String arg0, boolean arg1, boolean arg2, boolean arg3, Map<String, Object> arg4)
			throws IOException {
		delegate.queueDeclareNoWait(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.DeclareOk queueDeclarePassive(String arg0) throws IOException {
		return delegate.queueDeclarePassive(arg0);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String arg0) throws IOException {
		return delegate.queueDelete(arg0);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.DeleteOk queueDelete(String arg0, boolean arg1, boolean arg2)
			throws IOException {
		return delegate.queueDelete(arg0, arg1, arg2);
	}

	@Override
	public void queueDeleteNoWait(String arg0, boolean arg1, boolean arg2) throws IOException {
		delegate.queueDeleteNoWait(arg0, arg1, arg2);
	}

	@Override
	public PurgeOk queuePurge(String arg0) throws IOException {
		return delegate.queuePurge(arg0);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String arg0, String arg1, String arg2)
			throws IOException {
		return delegate.queueUnbind(arg0, arg1, arg2);
	}

	@Override
	public com.rabbitmq.client.AMQP.Queue.UnbindOk queueUnbind(String arg0, String arg1, String arg2,
			Map<String, Object> arg3) throws IOException {
		return delegate.queueUnbind(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean removeConfirmListener(ConfirmListener arg0) {
		return delegate.removeConfirmListener(arg0);
	}

	@Override
	public boolean removeReturnListener(ReturnListener arg0) {
		return delegate.removeReturnListener(arg0);
	}

	@Override
	public Command rpc(Method arg0) throws IOException {
		return delegate.rpc(arg0);
	}

	@Override
	public void setDefaultConsumer(Consumer arg0) {
		delegate.setDefaultConsumer(arg0);
	}

	@Override
	public CommitOk txCommit() throws IOException {
		return delegate.txCommit();
	}

	@Override
	public RollbackOk txRollback() throws IOException {
		return delegate.txRollback();
	}

	@Override
	public com.rabbitmq.client.AMQP.Tx.SelectOk txSelect() throws IOException {
		return delegate.txSelect();
	}

	@Override
	public boolean waitForConfirms() throws InterruptedException {
		return delegate.waitForConfirms();
	}

	@Override
	public boolean waitForConfirms(long arg0) throws InterruptedException, TimeoutException {
		return delegate.waitForConfirms(arg0);
	}

	@Override
	public void waitForConfirmsOrDie() throws IOException, InterruptedException {
		delegate.waitForConfirmsOrDie();
	}

	@Override
	public void waitForConfirmsOrDie(long arg0) throws IOException, InterruptedException, TimeoutException {
		delegate.waitForConfirmsOrDie(arg0);
	}
}
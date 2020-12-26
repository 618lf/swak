package com.swak.paxos.config;

/**
 * 服务器端的配置
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:50:49
 */
public class ServerConfig {

	/* TCP */
	private int recvBufferSizeTcp = 5242880;
	private int sendBufferSizeTcp = 5242880;
	private int maxPakageSizeTcp = 10485760;

	private int writeBufferHighWaterMark = 7340032;
	private int writeBufferLowWaterMark = 3145728;
	private int workerCountTcp = 8;
	private String sListenIp;
	private int listenPort;

	/* UDP */
	private int recvBufferSizeUdp = 1024 * 1024 * 32;
	private int sendBufferSizeUdp = 1024 * 1024 * 32;
	private int maxPakageSizeUdp = 1024 * 1024 * 32;
	private int workerCountUdp = 8;

	public int getRecvBufferSizeTcp() {
		return recvBufferSizeTcp;
	}

	public void setRecvBufferSizeTcp(int recvBufferSizeTcp) {
		this.recvBufferSizeTcp = recvBufferSizeTcp;
	}

	public int getSendBufferSizeTcp() {
		return sendBufferSizeTcp;
	}

	public void setSendBufferSizeTcp(int sendBufferSizeTcp) {
		this.sendBufferSizeTcp = sendBufferSizeTcp;
	}

	public int getMaxPakageSizeTcp() {
		return maxPakageSizeTcp;
	}

	public void setMaxPakageSizeTcp(int maxPakageSizeTcp) {
		this.maxPakageSizeTcp = maxPakageSizeTcp;
	}

	public int getWriteBufferHighWaterMark() {
		return writeBufferHighWaterMark;
	}

	public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
		this.writeBufferHighWaterMark = writeBufferHighWaterMark;
	}

	public int getWriteBufferLowWaterMark() {
		return writeBufferLowWaterMark;
	}

	public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
		this.writeBufferLowWaterMark = writeBufferLowWaterMark;
	}

	public int getWorkerCountTcp() {
		return workerCountTcp;
	}

	public void setWorkerCountTcp(int workerCountTcp) {
		this.workerCountTcp = workerCountTcp;
	}

	public String getsListenIp() {
		return sListenIp;
	}

	public void setsListenIp(String sListenIp) {
		this.sListenIp = sListenIp;
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public int getRecvBufferSizeUdp() {
		return recvBufferSizeUdp;
	}

	public void setRecvBufferSizeUdp(int recvBufferSizeUdp) {
		this.recvBufferSizeUdp = recvBufferSizeUdp;
	}

	public int getSendBufferSizeUdp() {
		return sendBufferSizeUdp;
	}

	public void setSendBufferSizeUdp(int sendBufferSizeUdp) {
		this.sendBufferSizeUdp = sendBufferSizeUdp;
	}

	public int getMaxPakageSizeUdp() {
		return maxPakageSizeUdp;
	}

	public void setMaxPakageSizeUdp(int maxPakageSizeUdp) {
		this.maxPakageSizeUdp = maxPakageSizeUdp;
	}

	public int getWorkerCountUdp() {
		return workerCountUdp;
	}

	public void setWorkerCountUdp(int workerCountUdp) {
		this.workerCountUdp = workerCountUdp;
	}
}

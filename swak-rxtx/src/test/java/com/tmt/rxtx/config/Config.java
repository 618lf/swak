package com.tmt.rxtx.config;

import gnu.io.SerialPort;

/**
 * 基础的配置
 * 
 * @author lifeng
 */
public interface Config {

	// 基本的配置
	int baudRate = 9600; // 9600波特率
	int dataBit = SerialPort.DATABITS_8; // 8 数据位
	int stopBit = SerialPort.STOPBITS_1; // 1 停止位
	int parityBit = SerialPort.PARITY_NONE; // 0 校验位
	Integer readBufferSize = 8192;
	Integer writeBufferSize = 1024;

	// 相关命令
	String CMD_Head = "AABBCC";
	String CMD_End = "DDEE";
	String CMD_Config = "A1";
	String CMD_StartCollection = "A2";
	String CMD_StartUpload = "A3";
	String CMD_StopUpload = "A4";
	String CMD_GetStatus = "A5";
	String CMD_GetMac = "A6";
	String CMD_GetPIN = "A7";
	String CMD_StopCollection = "A8";
	String CMD_Delete = "A9";
	String CMD_Reset = "AA";
}

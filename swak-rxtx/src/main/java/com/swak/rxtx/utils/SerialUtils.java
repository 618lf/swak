package com.swak.rxtx.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/**
 * @author han xinjian
 **/
@SuppressWarnings("unchecked")
public class SerialUtils {

	/**
	 * get all port list
	 *
	 * @return port list
	 */
	public static List<String> getCommNames() {
		Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> list = new ArrayList<String>();
		while (portIdentifiers.hasMoreElements()) {
			list.add(portIdentifiers.nextElement().getName());
		}
		return list.isEmpty() ? null : list;
	}

	/**
	 * connect SerialPort
	 *
	 * @param portName portName
	 * @param baudRate baudRate
	 * @param dataBits dataBits
	 * @param stopBits stopBits
	 * @param parity   parity
	 * @return SerialPort
	 * @throws Exception SerialPort connect err
	 */
	public static SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity)
			throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.err.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(SerialUtils.class.getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);

				return (SerialPort) commPort;
			} else {
				System.err.println("Error: Only serial ports are handled by this example.");
			}
		}
		return null;
	}

	/**
	 * connect SerialPort
	 *
	 * @param portName port name
	 * @param baudRate baudRate
	 * @return SerialPort
	 * @throws Exception SerialPort connect err
	 */
	public static SerialPort connect(String portName, int baudRate) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.err.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(SerialUtils.class.getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				return (SerialPort) commPort;
			} else {
				System.err.println("Error: Only serial ports are handled by this example.");
			}
		}
		return null;
	}

}

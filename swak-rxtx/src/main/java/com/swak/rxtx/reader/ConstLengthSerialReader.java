package com.swak.rxtx.reader;

import java.io.IOException;

import com.swak.rxtx.SerialContext;

/**
 * @author han xinjian
 **/
public class ConstLengthSerialReader implements SerialReader {

	private int length;

	private int index = 0;

	private byte[] bytes;

	@Override
	public byte[] readBytes() {
		for (; index < length; index++) {
			try {
				int read = SerialContext.getSerialPort().getInputStream().read();
				if (read == -1) {
					break;
				} else {
					bytes[index] = (byte) read;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (index == length) {
			index = 0;
			return bytes;
		}
		return null;
	}

	public ConstLengthSerialReader() {
		length = 24;
		bytes = new byte[length];
	}

	ConstLengthSerialReader(int length) {
		this.length = length;
		bytes = new byte[length];
	}

}

package com.swak.rxtx;

import java.util.List;

import com.swak.rxtx.utils.SerialUtils;

public class SerialTest {

	public static void main(String[] args) {
		List<String> comms = SerialUtils.getCommNames();
		if (comms != null) {
			for (String comm : comms) {
				System.out.println(comm);
			}
		}
	}

}

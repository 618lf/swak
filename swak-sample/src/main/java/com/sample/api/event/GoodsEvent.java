package com.sample.api.event;

import java.io.Serializable;

/**
 * Goods Event
 * 
 * @author lifeng
 */
public class GoodsEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public static GoodsEvent of() {
		return new GoodsEvent();
	}
}
package com.swak.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * 订单测试
 * 
 * @ClassName: Order
 * @author: lifeng
 * @date: Nov 14, 2019 11:50:48 AM
 */
@Getter
@Setter
@XmlRootElement
public class Order {

	private String id;
	private String name;
}

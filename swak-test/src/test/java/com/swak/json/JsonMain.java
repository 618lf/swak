package com.swak.json;

import com.swak.utils.JsonMapper;

/**
 * Json 的测试
 * 
 * @author lifeng
 * @date 2020年5月12日 下午5:08:44
 */
public class JsonMain {

	public static void main(String[] args) {
		StringOrder sorder = new StringOrder();
		sorder.setId("1024");
		sorder.setName("我的订单");
		String sjson = JsonMapper.toJson(sorder);
		System.out.println(sjson);
		StringOrder sorder2 = JsonMapper.fromJson(sjson, StringOrder.class);
		System.out.println(sorder2.getId());
		
		LongOrder lorder = new LongOrder();
		lorder.setId(1024L);
		lorder.setName("我的订单2");
		String ljson = JsonMapper.toJson(lorder);
		System.out.println(ljson);
		LongOrder lorder2 = JsonMapper.fromJson(ljson, LongOrder.class);
		System.out.println(lorder2.getId());
	}
}

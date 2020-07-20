package com.swak.rx.v1;

import com.swak.rx.Data;

public class TestApi {

	public static void main(String[] args) {
		Api api = new ApiImpl();
		Data data = api.get();
		api.save(data);
	}
}

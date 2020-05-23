package com.swak.loadbalance;

import java.util.List;

import com.swak.loadbalance.impl.ConfigurableWeightLoadBalance;
import com.swak.utils.Lists;

/**
 * 测试负载均衡
 * 
 * @author lifeng
 * @date 2020年4月30日 下午2:38:57
 */
public class TestLoadbalance {

	public static void main(String[] args) {

		// 资源
		List<Client> clients = Lists.newArrayList();
		clients.add(new Client());
		clients.add(new Client());

		LoadBalance<Client> loadBalance = new ConfigurableWeightLoadBalance<Client>("c_", "c_1:1, c_2:2");
		loadBalance.onRefresh(clients);

		// 选择到的资源
		for (int i = 0; i < 1000; i++) {
			loadBalance.select().doSometing();
		}

		// 打印资源使用情况
		for (Client client : clients) {
			System.out.println(client.count.get());
		}
	}
}
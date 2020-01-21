package com.swak.rxtx;

import com.swak.rxtx.channel.Channel;
import com.swak.rxtx.channel.ChannelHandler;

public class TestMain {

	public static void main(String[] args) {

		Channels.builder().setWorks(1).setHeartbeatSeconds(10).setChannelInit((channel) -> {
			channel.pipeline().addLast(new ChannelHandler() {

				@Override
				public void read(Channel channel, Object data) {
					super.read(channel, data);
				}

				@Override
				public void write(Channel channel, Object data) {
					super.write(channel, data);
				}

				@Override
				public void heartbeat(Channel channel) {
					channel.write("");
				}
			});
		}).build();
	}
}

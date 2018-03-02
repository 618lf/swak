package com.tmt.disruptor;

import com.lmax.disruptor.EventFactory;

public class MsgDataFactory implements EventFactory<MsgData>{

	@Override
	public MsgData newInstance() {
		return new MsgData();
	}
}

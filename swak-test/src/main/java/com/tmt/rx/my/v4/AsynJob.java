package com.tmt.rx.my.v4;

import com.tmt.rx.my.v3.CallBack;

public abstract class AsynJob<T> {
	public abstract void start(CallBack<T> callback);
}

package com.tmt.manage.logger;

import com.tmt.manage.App;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

/**
 * 打印输出
 * 
 * @author lifeng
 */
public class LoggerAppender extends AppenderBase<ILoggingEvent>{

	protected Layout<ILoggingEvent> layout;
	
	@Override
	protected void append(ILoggingEvent event) {
		App.APP.log(layout.doLayout(event));
	}
	public Layout<ILoggingEvent> getLayout() {
		return layout;
	}
	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}
}
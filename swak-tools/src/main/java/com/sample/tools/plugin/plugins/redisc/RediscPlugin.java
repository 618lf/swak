package com.sample.tools.plugin.plugins.redisc;

import com.sample.tools.plugin.Plugin;
import com.swak.fx.support.Display;

import javafx.scene.image.Image;

/**
 * redis 客户端
 * 
 * @author lifeng
 * @date 2020年6月3日 上午10:10:13
 */
public class RediscPlugin implements Plugin {

	@Override
	public Image logo() {
		return new Image(Display.load(RediscPlugin.class, "logo.png").toExternalForm());
	}

	@Override
	public String text() {
		return "Redis 管理";
	}

	@Override
	public void action() {
		System.out.println("点击事件");
	}
}

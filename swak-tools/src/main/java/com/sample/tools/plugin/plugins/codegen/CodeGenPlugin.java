package com.sample.tools.plugin.plugins.codegen;

import com.sample.tools.plugin.Plugin;
import com.swak.fx.support.Display;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 代码生成插件
 * 
 * @author lifeng
 * @date 2020年6月2日 下午4:00:44
 */
public class CodeGenPlugin implements Plugin {

	Stage page = null;

	@Override
	public Image logo() {
		return new Image(Display.load(CodeGenPlugin.class, "logo.png").toExternalForm());
	}

	@Override
	public String text() {
		return "代码生成";
	}

	@Override
	public void action() {
		if (page == null) {
			page = new Page().openOn(Display.getStage());
		}
		if (!page.isShowing()) {
			page.show();
		}
	}
}
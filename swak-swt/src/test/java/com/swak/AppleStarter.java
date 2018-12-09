package com.swak;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;

import com.swak.manage.App;
import com.swak.manage.widgets.Theme;
import com.swak.manage.widgets.theme.apple.AppleTheme;

/**
 * 使用苹果的样式
 * 
 * @author lifeng
 */
public class AppleStarter extends App {

	/**
	 * 设置支持的命令
	 */
	@Override
	protected void commands() {

	}

	/**
	 * 主题
	 */
	@Override
	protected Theme theme() {
		return new AppleTheme() {
			@Override
			public Action logo() {
				return Action.me().image(this.load("theme/税务.png"));
			}
			
			@Override
			public Action background() {
				return Action.me().color(new Color(0, 153, 204));
			}
			
			@Override
			public List<Action> actions() {
				return null;
			}
			
			// 加载资源
			private Image load(String image) {
				return Toolkit.getDefaultToolkit().getImage(AppleStarter.class.getResource(image));
			}
		};
	}

	/**
	 * 启动服务
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new AppleStarter().run(new String[] { "com/swak/AppleStarter.class" });
	}
}

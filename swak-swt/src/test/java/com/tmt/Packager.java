package com.tmt;

import org.eclipse.swt.graphics.Image;

import com.tmt.manage.App;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme;
import com.tmt.manage.widgets.theme.packager.PackagerTheme;

/**
 * 打包服务
 * 
 * @author lifeng
 */
public class Packager extends App {

	/**
	 * 定义主题
	 */
	@Override
	protected Theme theme() {
		return new PackagerTheme() {
			
			/**
			 * 设置logo
			 */
			@Override
			public Action logo() {
				return Action.me().image(this.load("logo.png"));
			}

			/**
			 * 加载图片
			 * 
			 * @param path
			 * @return
			 */
			private Image load(String path) {
				return ResourceManager.getImage(Packager.class, "theme/" + path);
			}
		};
	}

	@Override
	protected void commands() {
	}

	public static void main(String[] args) {
		new Packager().run(new String[] { "com/tmt/Packager.class" });
	}
}

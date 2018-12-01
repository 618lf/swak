package com.tmt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.tmt.commands.ExitCommand;
import com.tmt.commands.StartCommand;
import com.tmt.commands.StopCommand;
import com.tmt.commands.TouchCommand;
import com.tmt.commands.UpgraderCommand;
import com.tmt.commands.UrlCommand;
import com.tmt.manage.App;
import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme;
import com.tmt.manage.widgets.theme.orange.OrangeTheme;

/**
 * 测试启动
 * 
 * @author lifeng
 */
public class Starter extends App {

	/**
	 * 注册命令
	 */
	@Override
	protected void commands() {
		Commands.registers();
		Commands.register(Cmd.start, new StartCommand());
		Commands.register(Cmd.stop, new StopCommand());
		Commands.register(Cmd.exit, new ExitCommand());
		Commands.register(Cmd.task, new TouchCommand());
		Commands.register(Cmd.url, new UrlCommand());
		Commands.register(Cmd.upgrader, new UpgraderCommand());
	}

	/**
	 * 后期可以自定义主题
	 */
	@Override
	protected Theme theme() {

		return new OrangeTheme() {

			/**
			 * logo 的图片
			 */
			@Override
			public Action logo() {
				return Action.me().image(this.load("税务.png"));
			}

			/**
			 * 背景图， 确定一个比例就好： 16：9的比例
			 */
			@Override
			public Action background() {
				return Action.me().color(ResourceManager.getColor(new RGB(0, 153, 204)));
			}
			
			/**
			 * 背景图， 确定一个比例就好： 16：9的比例
			 */
			@Override
			public Action secure() {
				return Action.me().image(this.load("安全.png")).name("安全");
			}
			
			/**
			 * 背景图， 确定一个比例就好： 16：9的比例
			 */
			@Override
			public Action tray() {
				return Action.me().image(this.load("托盘.png")).name("最小化到托盘");
			}
			
			/**
			 * 背景图， 确定一个比例就好： 16：9的比例
			 */
			@Override
			public Action resize() {
				return Action.me().image(this.load("最大化.png")).imageOn(this.load("最小化.png"));
			}
			
			/**
			 * 背景图， 确定一个比例就好： 16：9的比例
			 */
			@Override
			public Action close() {
				return Action.me().image(this.load("关闭.png")).name("关闭");
			}
			
			/**
			 * 10-12英寸（上网本）1024×600、1366×768 13.3-15.6英寸大部分是1366×768 13英寸有1280×800、1600×900
			 * 14英寸也有1024×768（已淘汰）、1440×900、1600×900的高分屏 15英寸有1600×900、1920×1080（单屏最高分辨率）
			 * 还有18、19英寸的，分辨率也是1920×1080
			 */
			@Override
			public Point getShellSize(Rectangle clientArea) {
				int height = clientArea.height;
				int shellHeight = (int) (height * 0.85);
				int shellWight = shellHeight / 10 * 14;
				return new Point(shellWight, shellHeight);
			}

			/**
			 * 按钮
			 */
			@Override
			public List<Action> actions() {
				List<Action> actions = new ArrayList<>();
				actions.add(Action.me().image(this.load("首页.png")).imageOn(this.load("首页-on.png")).click(() -> {
					Commands.nameCommand(Cmd.url).exec("index");
				}));
				actions.add(Action.me().image(this.load("会员.png")).imageOn(this.load("会员-on.png")).click(() -> {
					Commands.nameCommand(Cmd.url).exec("member");
				}));
				actions.add(Action.me().image(this.load("订单.png")).imageOn(this.load("订单-on.png")).click(() -> {
					Commands.nameCommand(Cmd.url).exec("order");
				}));
				actions.add(Action.me().image(this.load("提醒.png")).imageOn(this.load("提醒-on.png")).click(() -> {
					Commands.nameCommand(Cmd.url).exec("notice");
				}));
				actions.add(Action.me().image(this.load("设置.png")).imageOn(this.load("设置-on.png")).click(() -> {
					Commands.nameCommand(Cmd.url).exec("settings");
				}));
				actions.add(Action.me().image(this.load("升级.png")).imageOn(this.load("升级-on.png")).click(() -> {
					Commands.nameCommand(Cmd.upgrader).exec();
				}));
				actions.add(Action.me().image(this.load("退出.png")).imageOn(this.load("退出-on.png")).click(() -> {
					Commands.nameCommand(Cmd.close).exec();
				}));
				return actions;
			}

			/**
			 * 加载图片
			 * 
			 * @param path
			 * @return
			 */
			private Image load(String path) {
				return ResourceManager.getImage(Starter.class, "theme/" + path);
			}
			
			
			
			@Override
			public boolean showTop() {
				return Boolean.FALSE;
			}

			@Override
			public boolean showTools() {
				return Boolean.FALSE;
			}

			@Override
			public boolean showFoot() {
				return Boolean.FALSE;
			}
		};
	}

	/**
	 * 启动服务
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Starter().run(new String[] { "com/tmt/Starter.class" });
	}
}

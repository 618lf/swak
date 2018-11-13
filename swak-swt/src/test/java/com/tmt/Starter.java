package com.tmt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.tmt.commands.ExitCommand;
import com.tmt.commands.StartCommand;
import com.tmt.commands.StopCommand;
import com.tmt.commands.TouchCommand;
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
				return Action.me().image(this.load("logo.png"));
			}

			/**
			 * 背景图
			 */
			@Override
			public Action background() {
				return Action.me().image(this.load("背景.png"));
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
				return ResourceManager.getImage(Starter.class, "theme/"+path);
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

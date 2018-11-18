package com.tmt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.tmt.manage.App;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme;
import com.tmt.manage.widgets.theme.upgrade.UpgraderTheme;

/**
 * 测试升级程序
 * 
 * @author lifeng
 */
public class Upgrader extends App {

	@Override
	protected Theme theme() {
		return new UpgraderTheme() {

			@Override
			public Action background() {
				return Action.me().image(this.load("背景.png"));
			}

			@Override
			public Action logo() {
				return Action.me().image(this.load("logo.png"));
			}

			@Override
			public Action close() {
				return Action.me().image(this.load("关闭.png"));
			}

			/**
			 * 设置按钮，仅仅支持三个按钮
			 */
			@Override
			public List<Action> actions() {
				List<Action> actions = new ArrayList<>();
				actions.add(Action.me().name("安装补丁").color(ResourceManager.getColor(SWT.COLOR_GRAY)));
				actions.add(Action.me().name("系统备份").color(ResourceManager.getColor(SWT.COLOR_GRAY)));
				actions.add(Action.me().name("选择补丁"));
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

			/**
			 * 已经安装的补丁
			 * 
			 * @return
			 */
			@Override
			public List<Patch> dones() {
				List<Patch> patchs = new ArrayList<>(10);
				patchs.add(Patch.newPatch("PTMS.1.0.zip", "添加新的功能", "2017-08-06"));
				patchs.add(Patch.newPatch("PTMS.0.3.zip", "添加新的功能", "2017-03-06"));
				patchs.add(Patch.newPatch("PTMS.0.2.zip", "添加新的功能", "2017-02-06"));
				patchs.add(Patch.newPatch("PTMS.0.1.zip", "添加新的功能", "2017-01-06"));
				return patchs;
			}
			
			/**
			 * 已经安装的补丁
			 * 
			 * @return
			 */
			@Override
			public List<Backup> backups() {
				List<Backup> patchs = new ArrayList<>(10);
				patchs.add(Backup.newBackup("PTMS.1.0.zip", "添加新的功能", "2017-08-06"));
				patchs.add(Backup.newBackup("PTMS.0.3.zip", "添加新的功能", "2017-03-06"));
				patchs.add(Backup.newBackup("PTMS.0.2.zip", "添加新的功能", "2017-02-06"));
				patchs.add(Backup.newBackup("PTMS.0.1.zip", "添加新的功能", "2017-01-06"));
				return patchs;
			}
		};
	}

	@Override
	protected void commands() {

	}

	public static void main(String[] args) {
		new Upgrader().run(new String[] { "com/tmt/Upgrader.class" });
	}
}
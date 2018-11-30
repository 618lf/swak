package com.tmt;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.tmt.manage.App;
import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.impl.UpgradeCommand;
import com.tmt.manage.config.Settings;
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
			@SuppressWarnings("unchecked")
			public List<Action> actions() {
				List<Action> actions = new ArrayList<>();
				actions.add(Action.me().name("安装补丁").colorOn(ResourceManager.getColor(244, 244, 238))
						.color(ResourceManager.getColor(237, 234, 215)).accept((files) -> {
							savePacks((List<File>) files);
						}));
				actions.add(Action.me().name("系统备份").colorOn(ResourceManager.getColor(244, 244, 238))
						.color(ResourceManager.getColor(237, 234, 215)).accept((files) -> {

						}));
				actions.add(Action.me().name("启动系统"));
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
			public List<Patch> patchs() {
				List<Patch> patchs = new ArrayList<>(10);
				File upgradeFile = new File(Settings.me().getUnUpgradePath());
				File[] files = upgradeFile.listFiles();
				if (files != null) {
					for (File file : files) {
						patchs.add(Patch.newPatch(file.getName(), "待安装"));
					}
				}

				upgradeFile = new File(Settings.me().getDoUpgradePath());
				files = upgradeFile.listFiles();
				if (files != null) {
					for (File file : files) {
						patchs.add(Patch.newPatch(file.getName(), "已安装"));
					}
				}
				patchs.sort(Patch.show);
				return patchs;
			}

			/**
			 * 备份数据的列表
			 * 
			 * @return
			 */
			@Override
			public List<Backup> backups() {
				List<Backup> patchs = new ArrayList<>(10);

				return patchs;
			}

			// 保存补丁文件
			private void savePacks(List<File> files) {
				File upgradeFile = new File(Settings.me().getUnUpgradePath());
				if (!upgradeFile.exists()) {
					upgradeFile.mkdirs();
				}
				for (File src : files) {
					try {
						File target = new File(upgradeFile, src.getName());
						if (target.exists()) {
							continue;
						}
						if (target.createNewFile()) {
							Files.copy(src.toPath(), target.toPath());
						}
					} catch (Exception e) {
					}
				}
			}
		};
	}

	@Override
	protected void commands() {
        Commands.register(Cmd.upgrade, new UpgradeCommand());
	}

	public static void main(String[] args) {
		new Upgrader().run(new String[] { "com/tmt/Upgrader.class" });
	}
}
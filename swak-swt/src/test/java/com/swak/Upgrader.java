package com.swak;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.graphics.Image;

import com.swak.commands.StarterCommand;
import com.swak.manage.App;
import com.swak.manage.command.Commands;
import com.swak.manage.command.Commands.Cmd;
import com.swak.manage.command.impl.UpgradeCommand;
import com.swak.manage.config.Settings;
import com.swak.manage.widgets.ResourceManager;
import com.swak.manage.widgets.Theme;
import com.swak.manage.widgets.theme.upgrade.Backup;
import com.swak.manage.widgets.theme.upgrade.Log;
import com.swak.manage.widgets.theme.upgrade.Patch;
import com.swak.manage.widgets.theme.upgrade.UpgraderTheme;

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
				actions.add(Action.me().name("数据备份").colorOn(ResourceManager.getColor(244, 244, 238))
						.color(ResourceManager.getColor(237, 234, 215)).accept((files) -> {
							saveBacks((Backup) files);
						}));
				actions.add(Action.me().name("运行日志").colorOn(ResourceManager.getColor(244, 244, 238))
						.color(ResourceManager.getColor(237, 234, 215)).accept((files) -> {
							saveLogs((File) files);
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

			/**
			 * 已经安装的补丁
			 * 
			 * @return
			 */
			@Override
			public List<Patch> patchs() {

				// 待安装
				List<Patch> patchs = new ArrayList<>(10);
				File upgradeFile = new File(Settings.me().getUnUpgradePath());
				File[] files = upgradeFile.listFiles();
				if (files != null) {
					for (File file : files) {
						patchs.add(Patch.newPatch(file.getName(), "待安装"));
					}
				}

				// 加载log
				if (files != null && files.length > 0) {
					List<Log> logs = this.read();
					for (Log log : logs) {
						for (Patch patch : patchs) {
							if (log.getName().equals(patch.getName())) {
								patch.setRemarks(log.getRemarks());
							}
						}
					}
				}

				// 已安装
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

			// 读取日志文件
			private List<Log> read() {
				List<Log> logs = new ArrayList<>();
				RandomAccessFile rf = null;
				try {
					List<String> lines = new ArrayList<>();
					File logFile = new File(Settings.me().getLogUpgradePath());
					rf = new RandomAccessFile(logFile, "r");
					String line = null;
					while ((line = rf.readLine()) != null) {
						lines.add(new String(line.getBytes("ISO-8859-1"), "utf-8"));
					}
					for (String _line : lines) {
						if (_line != null && !"".equals(_line)) {
							logs.add(Log.parse(_line));
						}
					}
				} catch (Exception e) {
				} finally {
					try {
						if (rf != null)
							rf.close();
					} catch (IOException e) {
					}
				}
				return logs;
			}

			/**
			 * 备份数据的列表
			 * 
			 * @return
			 */
			@Override
			public List<Backup> backups() {
				List<Backup> patchs = new ArrayList<>(10);
				File upgradeFile = new File(Settings.me().getBackupPath());
				File[] files = upgradeFile.listFiles();
				if (files != null) {
					for (File file : files) {
						patchs.add(Backup.newBackup(file));
					}
				}
				patchs.sort(Backup.show);
				return patchs;
			}

			/**
			 * 显示日志文件
			 */
			@Override
			public List<Log> logs() {
				List<Log> logs = new ArrayList<>();
				File errorDir = new File(Settings.me().getLogsPath(), "error");
				if (errorDir.exists()) {
					File[] files = errorDir.listFiles();
					for (File file : files) {
						logs.add(Log.newLog(file.getName(), ""));
					}
				}
				logs.sort(Log.show);
				return logs;
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
						Files.copy(src.toPath(), target.toPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 保存备份文件
			private void saveBacks(Backup backup) {
				File backFile = backup.getFile();
				try {
					File target = new File(backup.getSave(), backFile.getName());
					if (target.exists()) {
						return;
					}
					Files.copy(backFile.toPath(), target.toPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 保存日志文件
			private void saveLogs(File save) {
				List<Log> logs = this.logs();
				if (logs == null || logs.size() == 0) {
					return;
				}
				try {
					File errorDir = new File(Settings.me().getLogsPath(), "error");
					File zipFile = new File(save, "系统运行日志.zip");
					ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
					int max = 60;
					for (Log log : logs) {
						File logFile = new File(errorDir, log.getName());
						out.putNextEntry(new ZipEntry(logFile.getName()));
						out.write(Files.readAllBytes(logFile.toPath()));
						out.closeEntry();
						if (max-- <= 0) {
							break;
						}
					}
					out.flush();
					out.close();
				} catch (Exception e) {
				}
			}
		};
	}

	@Override
	protected void commands() {
		Commands.register(Cmd.upgrader, new UpgradeCommand());
		Commands.register(Cmd.starter, new StarterCommand());
	}

	public static void main(String[] args) {
		new Upgrader().run(new String[] { "com/tmt/Upgrader.class" });
	}
}
package com.swak.tools.operation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

import com.swak.tools.operation.OpsFile.OpsEntry;

/**
 * 抽象的操作
 * 
 * @author lifeng
 */
public abstract class AbsOps implements Ops {

	Ops next;

	/**
	 * 一步一步往下执行
	 */
	@Override
	public void doOps(OpsFile file) {

		// 执行本次任务
		try {
			doInnerOps(file);
		} catch (Exception e) {
			file.error(e.getMessage());
		}

		// 执行下一个任务
		if (next != null) {
			next.doOps(file);
		}
	}

	/**
	 * 设置下一个执行器
	 * 
	 * @param next
	 */
	public void next(Ops next) {
		this.next = next;
	}

	/**
	 * 子类需要完成的
	 * 
	 * @param file
	 * @return
	 */
	protected abstract void doInnerOps(OpsFile file) throws Exception;

	/**
	 * 更新文件
	 * 
	 * @param parent
	 * @param entry
	 * @throws IOException
	 */
	protected void updateFile(File parent, OpsEntry entry) throws IOException {
		File libFile = new File(parent, entry.getName());
		if (libFile.exists()) {
			libFile.delete();
			libFile.createNewFile();
		} else {
			libFile.getParentFile().mkdirs();
			libFile.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(libFile);
		try {
			out.write(entry.getData());
		} finally {
			out.close();
		}
	}

	// 删除子文件夹
	protected boolean deleteChildFile(File dirFile) {
		if (!dirFile.exists()) {
			return false;
		}
		if (dirFile.isFile()) {
			return dirFile.delete();
		} else {
			for (File file : dirFile.listFiles()) {
				deleteFile(file);
			}
		}
		return true;
	}

	// 删除整个文件夹
	protected boolean deleteFile(File dirFile) {
		if (!dirFile.exists()) {
			return false;
		}
		if (dirFile.isFile()) {
			return dirFile.delete();
		} else {
			for (File file : dirFile.listFiles()) {
				deleteFile(file);
			}
		}
		return dirFile.delete();
	}

	/**
	 * 复制目录
	 * 
	 * @param targetDir
	 * @param sourceDir
	 */
	protected void copyFile(File targetDir, File sourceDir, FilenameFilter filter) {
		try {
			targetDir.mkdirs();
			File[] files = sourceDir.listFiles(filter);
			for (File file : files) {
				if (file.isFile()) {
					File temp = new File(targetDir, file.getName());
					Files.copy(file.toPath(), temp.toPath());
				} else {
					this.copyFile(new File(targetDir, file.getName()), file, filter);
				}
			}
		} catch (Exception e) {
		}
	}
}
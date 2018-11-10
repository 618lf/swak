package com.tmt.manage.widgets;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * 
 * 进度条
 * 
 * @author lifeng
 */
public class Progress {

	private ProgressBar progressBar;
	private Thread thread;
	private volatile int times;
	private volatile boolean finish;

	public Progress(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * 开始
	 */
	public void start() {
		finish = false;
		times = 0;
		Display.getDefault().asyncExec(() -> {
			this.progressBar.setSelection(0);
			this.progressBar.setVisible(true);
		});
		thread = new Thread(() -> {
			while (!finish) {
				Display.getDefault().asyncExec(() -> {
					int selection = this.progressBar.getSelection();
					if (selection <= 50) {
						this.progressBar.setSelection(selection + 5);
					} else if (selection <= 75) {
						times++;
						if (times >= 5) {
							this.progressBar.setSelection(selection + 3);
							times = 0;
						}
					} else if (selection <= 95) {
						times++;
						if (times >= 10) {
							this.progressBar.setSelection(selection + 1);
							times = 0;
						}
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Display.getDefault().asyncExec(() -> {
						this.progressBar.setSelection(100);
						this.progressBar.setVisible(false);
					});
					finish = true;
					break;
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 结束
	 */
	public void stop() {
		if (thread != null) {
			thread.interrupt();
		}
	}
}
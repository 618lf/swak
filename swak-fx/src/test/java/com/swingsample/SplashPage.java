package com.swingsample;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.swak.swing.support.AbstractPage;
import com.swak.swing.support.Display;

/**
 * 启动页
 * 
 * @author lifeng
 * @date 2020年5月21日 上午11:03:31
 */
public class SplashPage extends AbstractPage {

	JFrame root;
	JLabel logo_image;
	JLabel logo_text;
	JProgressBar progress;
	JLabel tip;

	private volatile Thread thread;
	private volatile int times;
	private volatile boolean finish;

	@Override
	public void initialize() {

		// root
		root = new JFrame();
		root.setBounds(0, 0, 500, 400);
		root.setLocationRelativeTo(null);
		root.setUndecorated(true);
		root.setForeground(Color.WHITE);
		root.getContentPane().setBackground(Color.decode("#3175af"));
		root.getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.decode("#3175af")));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0 };
		root.getContentPane().setLayout(gridBagLayout);
		root.setIconImage(new ImageIcon(Display.load("/images/logo.png")).getImage());
		root.setTitle("个税易 .云端");

		// 占位
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		root.getContentPane().add(panel, gbc_panel);

		// logo
		logo_image = new JLabel();
		ImageIcon _logo_image = new ImageIcon(Display.load("/images/logo.png"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
		logo_image.setAlignmentX(Component.CENTER_ALIGNMENT);
		logo_image.setHorizontalAlignment(SwingConstants.CENTER);
		logo_image.setIcon(_logo_image);
		logo_image.setPreferredSize(new Dimension(0, 120));
		gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		root.getContentPane().add(logo_image, gbc_panel);

		logo_text = new JLabel("个税易 .云端");
		logo_text.setAlignmentX(Component.CENTER_ALIGNMENT);
		logo_text.setHorizontalAlignment(SwingConstants.CENTER);
		logo_text.setPreferredSize(new Dimension(0, 80));
		logo_text.setFont(new Font("宋体", Font.PLAIN, 18));
		logo_text.setForeground(Color.WHITE);
		gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		root.getContentPane().add(logo_text, gbc_panel);

		// 进度
		progress = new JProgressBar();
		progress.setPreferredSize(new Dimension(0, 20));
		progress.setBackground(Color.WHITE);
		progress.setBorderPainted(false);
		progress.setForeground(Color.decode("#3175af"));
		progress.setBorder(null);
		gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		root.getContentPane().add(progress, gbc_panel);

		// 进度 - 文字
		tip = new JLabel("请稍等，正在加载...");
		tip.setAlignmentX(Component.CENTER_ALIGNMENT);
		tip.setHorizontalAlignment(SwingConstants.CENTER);
		tip.setPreferredSize(new Dimension(0, 80));
		tip.setBackground(Color.WHITE);
		tip.setOpaque(true);
		tip.setFont(new Font("宋体", Font.PLAIN, 14));
		gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		root.getContentPane().add(tip, gbc_panel);
		this.start();
		super.initialize();
	}

	@Override
	public void show() {
		root.setVisible(true);
	}

	@Override
	public void close() {
		root.dispose();
	}

	/**
	 * 开始
	 */
	public void start() {
		finish = false;
		times = 0;
		Display.runUI(() -> {
			this.progress.setValue(0);
		});
		thread = new Thread(() -> {
			while (!finish) {
				Display.runUI(() -> {
					double selection = this.progress.getValue() / 100.0;
					if (selection <= 0.5) {
						this.progress.setValue((int) ((selection + 0.05) * 100.0));
					} else if (selection <= 0.75) {
						times++;
						if (times >= 5) {
							this.progress.setValue((int) ((selection + 0.03) * 100.0));
							times = 0;
						}
					} else if (selection <= 0.95) {
						times++;
						if (times >= 10) {
							this.progress.setValue((int) ((selection + 0.01) * 100.0));
							times = 0;
						}
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			this.finish();
		});
		thread.setName("UI.Splash");
		thread.setDaemon(true);
		thread.start();
	}

	// 结束
	private void finish() {
		Display.runUI(() -> {
			this.progress.setValue(100);
			finish = true;
		});
		try {
			Thread.sleep(200);
		} catch (Exception e) {
		}
		closeFuture.complete(null);
	}

	/**
	 * 结束
	 */
	@Override
	public CompletableFuture<Void> waitClose() {
		return initFuture.thenCompose((v) -> {
			thread.interrupt();
			return closeFuture;
		});
	}
}

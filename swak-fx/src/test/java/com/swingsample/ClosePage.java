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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.swak.swing.support.AbstractPage;
import com.swak.swing.support.Display;

/**
 * 关闭
 * 
 * @author lifeng
 * @date 2020年5月22日 下午5:43:19
 */
public class ClosePage extends AbstractPage {

	JFrame root;
	JLabel logo_image;
	JLabel logo_text;

	private volatile Thread thread;
	private volatile boolean finish;

	@Override
	public void initialize() {
		// root
		root = new JFrame();
		root.setBounds(0, 0, 500, 100);
		root.setLocationRelativeTo(null);
		root.setUndecorated(true);
		root.setForeground(Color.WHITE);
		root.getContentPane().setBackground(Color.decode("#3175af"));
		root.getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#3175af")));
		root.setIconImage(new ImageIcon(Display.load("/images/logo.png")).getImage());
		root.setTitle("个税易 .云端");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		root.getContentPane().setLayout(gridBagLayout);

		ImageIcon _logo_image = new ImageIcon(Display.load("/images/loading.gif"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
		logo_image = new JLabel();
		logo_image.setPreferredSize(new Dimension(100, 100));
		logo_image.setIcon(_logo_image);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		root.getContentPane().add(logo_image, gbc_panel);

		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		root.getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		logo_text = new JLabel("数据备份中,清稍等...");
		logo_text.setAlignmentX(Component.CENTER_ALIGNMENT);
		logo_text.setHorizontalAlignment(SwingConstants.CENTER);
		logo_text.setPreferredSize(new Dimension(0, 100));
		logo_text.setFont(new Font("宋体", Font.PLAIN, 18));
		logo_text.setForeground(Color.WHITE);
		panel_1.add(logo_text);

		this.start();
		super.initialize();
	}

	/**
	 * 开始
	 */
	private void start() {
		finish = false;
		thread = new Thread(() -> {
			while (!finish) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			this.finish();
		});
		thread.setDaemon(true);
		thread.start();
	}

	// 结束
	private void finish() {
		finish = true;
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
		}
		logo_text.setText("数据备份成功，准备退出");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		closeFuture.complete(null);
	}

	@Override
	public void show() {
		this.root.setVisible(true);
	}

	@Override
	public void close() {
		this.root.dispose();
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

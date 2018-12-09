package com.swak.manage.widgets.theme.apple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.swak.manage.config.Settings;
import com.swak.manage.widgets.Theme;
import com.swak.manage.widgets.Window;

/**
 * 苹果主题
 * 
 * @author lifeng
 */
public class AppleApp extends JFrame implements Window {

	private static final long serialVersionUID = 1L;

	private AppleTheme theme;
	private AppleBrowser browser;

	public AppleApp() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.browser = new AppleBrowser();
		} catch (Exception e) {
		}
	}

	/**
	 * Launch the application.
	 */
	public void open() {
		EventQueue.invokeLater(() -> {
			this._open();
		});
	}

	// 显示界面
	private void _open() {
		initShell();
		createContents();
		this.dispose();
		this.setUndecorated(true);
		this.setResizable(true);
		this.setExtendedState(JFrame.ICONIFIED);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toFront();
		this.setVisible(true);
	}

	// 初始化shell
	protected void initShell() {
		this.setTitle(Settings.me().getServer().getName());
		this.setIconImage(theme.logo().image());
		this.setBackground(theme.background().color());
		Dimension clientArea = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle shellArea = this.getShellSize(clientArea);
		this.setSize(shellArea.width, shellArea.height);
		int width = clientArea.width;
		int height = clientArea.height;
		this.setLocation((width - shellArea.width) / 2, (height - shellArea.height) / 2);
		GridBagLayout shellLayout = new GridBagLayout();
		this.setLayout(shellLayout);
	}

	// 屏幕大小
	protected Rectangle getShellSize(Dimension dimension) {
		int height = dimension.height;
		int shellHeight = (int) (height * 0.85);
		int shellWight = shellHeight / 10 * 14;
		return new Rectangle(shellWight, shellHeight);
	}

	// 配置内容
	protected void createContents() {
		JPanel panel = new JPanel();
		this.configureTop(panel);
		JPanel content = new JPanel();
		this.configureContent(content);
		add(panel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setAnchor(GBC.EAST));
		add(content, new GBC(0, 1, 1, 1).setFill(GBC.BOTH).setWeight(1, 1));
	}

	// 顶部配置
	protected void configureTop(JPanel panel) {
		GridBagLayout topLayout = new GridBagLayout();
		panel.setLayout(topLayout);
		panel.setBackground(theme.background().color());
		ImageButton image = ImageButton.image(theme.logo().image());
		image.setSize(32, 32);
		image.display();
		panel.add(image, new GBC(0, 0));

		JLabel text = new JLabel();
		text.setText(Settings.me().getServer().getName());
		panel.add(text, new GBC(1, 0).setFill(GBC.BOTH).setWeight(1, 1));
	}

	// 内容配置
	protected void configureContent(JPanel content) {
		// CardLayout cardLayout = new CardLayout();
		// content.setLayout(cardLayout);
		//
		// // 日志
		// JTextArea sample = new JTextArea();
		// sample.setText("The quick brown fox jump over the lazy dog.");
		// sample.setBorder(BorderFactory.createEmptyBorder());
		// sample.setEditable(false);
		// sample.setLineWrap(true);
		// sample.setBorder(BorderFactory.createEtchedBorder());
		//
		// // 添加日志
		// content.add("log", sample);
		content.setLayout(new BorderLayout());
		// 设置浏览器
		// this.browser = new AppleBrowser();
		// content.add(browser.getUIComponent(), BorderLayout.CENTER);
		//
		// cardLayout.show(content, "brower");

		content.add(browser.getUIComponent(), BorderLayout.CENTER);
	}

	/**
	 * 设置主题
	 */
	@Override
	public void theme(Theme me) {
		this.theme = (AppleTheme) me;
	}
}

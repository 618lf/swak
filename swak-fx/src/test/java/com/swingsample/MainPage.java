package com.swingsample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.swak.swing.support.Dialogs;
import com.swak.swing.support.Display;
import com.swak.swing.support.Window;
import com.swak.ui.Event;
import com.swak.ui.EventListener;

/**
 * 主页
 * 
 * @author lifeng
 * @date 2020年5月21日 上午11:03:43
 */
public class MainPage extends Window implements EventListener {

	JLabel logo_image;
	JLabel logo_text;
	JPanel moveAble;
	JLabel ops_min;
	JLabel ops_max;
	JLabel ops_close;

	@Override
	public void initialize() {

		// root
		root = new JFrame();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(root.getGraphicsConfiguration());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		double h = (dim.getHeight() - screenInsets.bottom) * 0.93;
		double w = h * 1.66;
		root.setBounds(0, 0, (int) w, (int) h);
		root.setLocationRelativeTo(null);
		root.setUndecorated(true);
		root.setForeground(Color.WHITE);
		root.getContentPane().setBackground(Color.decode("#3175af"));
		root.getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.decode("#3175af")));
		root.setIconImage(new ImageIcon(Display.load("/images/logo.png")).getImage());
		root.getContentPane().setLayout(new BorderLayout(0, 0));
		root.setTitle("个税易 .云端");

		// top
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		root.getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		logo_image = new JLabel();
		ImageIcon _logo_image = new ImageIcon(Display.load("/images/logo.png"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
		logo_image.setIcon(_logo_image);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panel.add(logo_image, gbc_panel);

		logo_text = new JLabel("个税易 .云端");
		logo_text.setFont(new Font("宋体", Font.PLAIN, 18));
		logo_text.setForeground(Color.WHITE);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		panel.add(logo_text, gbc_panel);

		moveAble = new JPanel();
		moveAble.setOpaque(false);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		panel.add(moveAble, gbc_panel);

		ops_min = new JLabel();
		_logo_image = new ImageIcon(Display.load("/images/最小化.png"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
		ops_min.setIcon(_logo_image);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 0;
		panel.add(ops_min, gbc_panel);

		ops_max = new JLabel();
		_logo_image = new ImageIcon(Display.load("/images/最大化.png"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
		ops_max.setIcon(_logo_image);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 4;
		gbc_panel.gridy = 0;
		panel.add(ops_max, gbc_panel);

		ops_close = new JLabel();
		_logo_image = new ImageIcon(Display.load("/images/关闭.png"));
		_logo_image.setImage(_logo_image.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
		ops_close.setIcon(_logo_image);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 5;
		gbc_panel.gridy = 0;
		panel.add(ops_close, gbc_panel);

		// 初始化事件
		this.initEvent();

		// 注册事件
		Display.getEventBus().register(this);

		// 这个不能忘记
		super.initialize();
	}

	/**
	 * 设置事件
	 */
	private void initEvent() {
		this.ops_min.addMouseListener(new MinimizeListener());
		this.ops_max.addMouseListener(new MaximizeListener());
		this.ops_close.addMouseListener(new CloseListener());
		this.moveAble.addMouseListener(new MoveListener());
		this.moveAble.addMouseMotionListener(new MoveListener());
	}

	@Override
	public void listen(Event event) {
		if (event.is(Event.DOWNLOAD)) {
			System.out.println("下载事件");
		} else if (event.is(Event.URL)) {
			String url = event.getMessage();
			Display.runUI(() -> {
				System.out.println("显示页面：" + url);
			});
		}
	}

	@Override
	public void show() {
		if (!this.closed) {
			root.setVisible(true);
			root.setExtendedState(JFrame.NORMAL);
		}
	}

	@Override
	public void close() {
		this.closed = true;
		root.dispose();
	}

	@Override
	protected void onClose(MouseEvent e) {
		int result = Dialogs.confirm("提醒", "确认关闭？");
		if (result == 0) {
			super.onClose(e);
			Display.getEventBus().post(Event.EXIT);
		}
	}
}
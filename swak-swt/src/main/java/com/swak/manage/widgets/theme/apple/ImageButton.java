package com.swak.manage.widgets.theme.apple;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * awt 的 imahe button
 * 
 * @author lifeng
 */
public class ImageButton extends JLabel {

	private static final long serialVersionUID = 1L;
	private Image image;

	/**
	 * 定义构造
	 * 
	 * @param image
	 */
	public ImageButton(Image image) {
		super();
		this.image = image;
	}

	/**
	 * 显示图片
	 */
	public void display() {
		final Dimension lbounds = this.getSize();
		int width = lbounds.width; // 图片宽
		int height = lbounds.height; // 图片高
		Image image = this.drawable();
		ImageIcon icon = new ImageIcon(image);
		icon.setImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT));
		this.setIcon(icon);
	}

	/**
	 * 获得需要绘制的图片
	 * 
	 * @return
	 */
	private Image drawable() {
		return image;
	}

	/**
	 * 构造图片
	 * 
	 * @param image
	 * @return
	 */
	public static ImageButton image(Image image) {
		return new ImageButton(image);
	}
}
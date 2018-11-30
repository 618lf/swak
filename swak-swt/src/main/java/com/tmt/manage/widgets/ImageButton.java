package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * image button
 * 
 * @author lifeng
 */
public class ImageButton implements PaintListener {

	// 元素组件
	private Control control;
	private Object layout;
	private Image image;
	private Image hoverImage;
	private Image onImage;
	private Color onColor;
	private Color blurColor;
	private String text;
	private String tip;
	private Runnable click;
	private Rectangle bounds;
	private boolean label = true;
	private volatile boolean on = false;
	private volatile boolean hoverOn = false;
	private ImageButtonGroup group;

	/**
	 * 创建一个图片按钮
	 * 
	 * @param parent
	 */
	private ImageButton(Composite parent) {
		if (label) {
			this.control = new CLabel(parent, SWT.SHADOW_NONE);
		} else {
			this.control = new Button(parent, SWT.SHADOW_NONE);
		}
	}

	/**
	 * 返回控制器
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Control> T control() {
		return (T) this.control;
	}

	/**
	 * 自适应图片的显示
	 */
	@Override
	public void paintControl(PaintEvent pe) {

		// 绘制的图片
		Image image = this.drawable();

		// 绘制图片
		if (image != null) {
			final Rectangle ibounds = image.getBounds();
			int iwidth = ibounds.width; // 图片宽
			int iheight = ibounds.height; // 图片高

			final Rectangle lbounds = control.getBounds();
			int lwidth = lbounds.width; // 图片宽
			int lheight = lbounds.height; // 图片高
			double ratio = 1; // 缩放比率
			double r1 = lwidth * ratio / iwidth;
			double r2 = lheight * ratio / iheight;
			ratio = Math.min(r1, r2);
			pe.gc.drawImage(image, 0, 0, iwidth, iheight, 0, 0, (int) (iwidth * ratio), (int) (iheight * ratio));
		}

		// 切换颜色
		Color color = this.drawColor();

		// 显示底色
		if (color != null) {
			this.control.setBackground(color);
		}
	}

	/**
	 * 获得需要绘制的图片
	 * 
	 * @return
	 */
	private Image drawable() {
		if ((this.on || this.hoverOn) && (hoverImage != null || onImage != null)) {
			return hoverImage != null ? hoverImage : onImage;
		}
		return image;
	}

	/**
	 * 获得需要绘制的图片, 选中是白色
	 * 
	 * @return
	 */
	private Color drawColor() {
		if ((this.on || this.hoverOn) && this.onColor != null) {
			return onColor;
		}
		return blurColor;
	}

	/**
	 * 设置组
	 * 
	 * @param tip
	 * @return
	 */
	public ImageButton group(ImageButtonGroup group) {
		this.group = group;
		this.group.addImageButton(this);
		return this;
	}

	/**
	 * 设置文本
	 * 
	 * @param tip
	 * @return
	 */
	public ImageButton text(String text) {
		this.text = text;
		return this;
	}

	/**
	 * 设置提示
	 * 
	 * @param tip
	 * @return
	 */
	public ImageButton tip(String tip) {
		this.tip = tip;
		return this;
	}

	/**
	 * 设置图片
	 * 
	 * @param image
	 * @return
	 */
	public ImageButton image(Image image) {
		this.image = image;
		return this;
	}

	/**
	 * 布局
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton layout(Object layout) {
		this.layout = layout;
		return this;
	}

	/**
	 * 设置大小和位置
	 * 
	 * @param tip
	 * @return
	 */
	public ImageButton bounds(Rectangle bounds) {
		this.bounds = bounds;
		return this;
	}

	/**
	 * hover
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton hover(Image image) {
		this.hoverImage = image;
		return this;
	}

	/**
	 * hover
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton on(Image image) {
		this.onImage = image;
		return this;
	}
	
	/**
	 * hover
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton on(Color color) {
		this.onColor = color;
		return this;
	}

	/**
	 * hover
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton blur(Color color) {
		this.blurColor = color;
		return this;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param run
	 * @return
	 */
	public ImageButton click(Runnable click) {
		this.click = click;
		return this;
	}

	/**
	 * 点亮
	 */
	public void on() {
		this.on = true;
		control.redraw();
	}

	/**
	 * 取消点亮
	 */
	public void off() {
		this.on = false;
		control.redraw();
	}

	/**
	 * 点亮
	 */
	private void hoverOn() {
		this.hoverOn = true;
		control.redraw();
		control.getShell().setCursor(ResourceManager.getCursor(SWT.CURSOR_HAND));
	}

	/**
	 * 取消点亮
	 */
	private void hoverOff() {
		this.hoverOn = false;
		control.redraw();
		control.getShell().setCursor(ResourceManager.getCursor(SWT.CURSOR_ARROW));
	}

	/**
	 * 切还
	 */
	private void hoverToggle() {
		this.hoverOn = !this.hoverOn;
		control.redraw();
		control.getShell().setCursor(ResourceManager.getCursor(SWT.CURSOR_HAND));
	}

	/**
	 * 创建组件
	 */
	public ImageButton build() {

		// 设置位置
		if (layout != null) {
			this.control.setLayoutData(layout);
		}

		// 大小
		if (bounds != null) {
			this.control.setBounds(bounds);
		}

		// 显示的图片
		this.control.addPaintListener(this);

		// 文本
		if (this.text != null) {
			this.control.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
			if (control instanceof CLabel) {
				((CLabel) (this.control)).setText(text);
				((CLabel) (this.control)).setAlignment(SWT.CENTER);
			} else {
				((Button) (this.control)).setText(text);
				((Button) (this.control)).setAlignment(SWT.CENTER);
			}
		}

		// 文本 - blur
		if (this.text != null && this.blurColor != null) {
			this.control.setBackground(blurColor);
		}

		// 提示
		if (this.tip != null) {
			this.control.setToolTipText(this.tip);
		}

		// hover 事件
		if (this.hoverImage != null) {
			this.control.addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseEnter(MouseEvent arg0) {
					hoverOn();
				}

				@Override
				public void mouseExit(MouseEvent arg0) {
					hoverOff();
				}

				@Override
				public void mouseHover(MouseEvent arg0) {
				}
			});
		}

		// 点击事件
		if (this.click != null || this.group != null || this.onImage != null) {
			this.control.addListener(SWT.MouseUp, (e) -> {
				if (this.group != null) {
					this.group.on(this);
				} else if (this.onImage != null) {
					hoverToggle();
				}
				if (this.click != null) {
					this.click.run();
				}
			});
		}
		return this;
	}

	/**
	 * 创建一个图片按钮
	 * 
	 * @param parent
	 * @return
	 */
	public static ImageButton builder(Composite parent) {
		ImageButton imageButton = new ImageButton(parent);
		return imageButton;
	}
}

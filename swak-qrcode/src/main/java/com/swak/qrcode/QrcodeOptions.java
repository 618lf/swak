package com.swak.qrcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.EncodeHintType;
import com.swak.utils.StringUtils;

/**
 * 生成的属性
 * 
 * @author lifeng
 */
public class QrcodeOptions {

	/**
	 * 塞入二维码的信息
	 */
	private String msg;

	/**
	 * 二维码的背景图
	 */
	private BufferedImage background;

	/**
	 * 背景图宽
	 */
	private Integer bgW;

	/**
	 * 背景图高
	 */
	private Integer bgH;

	/**
	 * 二维码中间的logo
	 */
	private BufferedImage logo;

	/**
	 * logo的样式， 目前支持圆角+普通
	 */
	private LogoStyle logoStyle;

	/**
	 * logo 的边框背景色
	 */
	private Color logoBgColor;

	/**
	 * 生成二维码的宽
	 */
	private Integer w;

	/**
	 * 生成二维码的高
	 */
	private Integer h;

	/**
	 * 生成二维码的X坐标
	 */
	private Integer x;

	/**
	 * 生成二维码的Y坐标
	 */
	private Integer y;

	/**
	 * 生成二维码图片的格式 png, jpg
	 */
	private String picType;

	/**
	 * 绘制二维码的样式
	 */
	private DrawStyle drawStyle;

	/**
	 * 绘制二维码的图片链接
	 */
	private String drawImg;

	/**
	 * 其他的设置项目
	 */
	private Map<EncodeHintType, Object> hints;

	/**
	 * 输出的本地文件地址
	 */
	private File outFile;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public BufferedImage getBackground() {
		return background;
	}

	public void setBackground(BufferedImage background) {
		this.background = background;
	}

	public Integer getBgW() {
		return bgW;
	}

	public void setBgW(Integer bgW) {
		this.bgW = bgW;
	}

	public Integer getBgH() {
		return bgH;
	}

	public void setBgH(Integer bgH) {
		this.bgH = bgH;
	}

	public BufferedImage getLogo() {
		return logo;
	}

	public void setLogo(BufferedImage logo) {
		this.logo = logo;
	}

	public LogoStyle getLogoStyle() {
		return logoStyle;
	}

	public void setLogoStyle(LogoStyle logoStyle) {
		this.logoStyle = logoStyle;
	}

	public Color getLogoBgColor() {
		return logoBgColor;
	}

	public void setLogoBgColor(Color logoBgColor) {
		this.logoBgColor = logoBgColor;
	}

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public Integer getH() {
		return h;
	}

	public void setH(Integer h) {
		this.h = h;
	}

	public Map<EncodeHintType, Object> getHints() {
		return hints;
	}

	public void setHints(Map<EncodeHintType, Object> hints) {
		this.hints = hints;
	}

	public String getPicType() {
		return picType;
	}

	public void setPicType(String picType) {
		this.picType = picType;
	}

	public DrawStyle getDrawStyle() {
		return drawStyle;
	}

	public void setDrawStyle(DrawStyle drawStyle) {
		this.drawStyle = drawStyle;
	}

	public String getDrawImg() {
		return drawImg;
	}

	public void setDrawImg(String drawImg) {
		this.drawImg = drawImg;
	}

	public File getOutFile() {
		return outFile;
	}

	public void setOutFile(File outFile) {
		this.outFile = outFile;
	}

	public enum LogoStyle {
		ROUND, NORMAL;

		public static LogoStyle getStyle(String name) {
			if ("ROUND".equalsIgnoreCase(name)) {
				return ROUND;
			} else {
				return NORMAL;
			}
		}
	}

	public enum DrawStyle {
		RECT { // 矩形

			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				g2d.fillRect(x, y, size, size);
			}
		},
		CIRCLE {
			// 圆点
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				g2d.fill(new Ellipse2D.Float(x, y, size, size));
			}
		},
		TRIANGLE {
			// 三角形
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				int px[] = { x, x + (size >> 1), x + size };
				int py[] = { y + size, y, y + size };
				g2d.fillPolygon(px, py, 3);
			}
		},
		DIAMOND {
			// 五边形-钻石
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				int cell4 = size >> 2;
				int cell2 = size >> 1;
				int px[] = { x + cell4, x + size - cell4, x + size, x + cell2, x };
				int py[] = { y, y, y + cell2, y + size, y + cell2 };
				g2d.fillPolygon(px, py, 5);
			}
		},
		SEXANGLE {
			// 六边形
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				int add = size >> 2;
				int px[] = { x + add, x + size - add, x + size, x + size - add, x + add, x };
				int py[] = { y, y, y + add + add, y + size, y + size, y + add + add };
				g2d.fillPolygon(px, py, 6);
			}
		},
		OCTAGON {
			// 八边形
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				int add = size / 3;
				int px[] = { x + add, x + size - add, x + size, x + size, x + size - add, x + add, x, x };
				int py[] = { y, y, y + add, y + size - add, y + size, y + size, y + size - add, y + add };
				g2d.fillPolygon(px, py, 8);
			}
		},
		IMAGE {
			// 自定义图片
			@Override
			public void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img) {
				g2d.drawImage(img, x, y, size, size, null);
			}
		},;

		private static Map<String, DrawStyle> map;

		static {
			map = new HashMap<>(6);
			for (DrawStyle style : DrawStyle.values()) {
				map.put(style.name(), style);
			}
		}

		public static DrawStyle getDrawStyle(String name) {
			if (StringUtils.isBlank(name)) { // 默认返回矩形
				return RECT;
			}

			DrawStyle style = map.get(name.toUpperCase());
			return style == null ? RECT : style;
		}

		public abstract void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img);
	}
}

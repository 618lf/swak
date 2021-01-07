package com.swak.qrcode;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.swak.utils.StringUtils;

/**
 * 图片样式
 * 
 * @author lifeng
 * @date 2021年1月5日 下午4:05:28
 */
public enum ImageStyle {
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

	private static Map<String, ImageStyle> map;

	static {
		map = new HashMap<>(6);
		for (ImageStyle style : ImageStyle.values()) {
			map.put(style.name(), style);
		}
	}

	public static ImageStyle getImageStyle(String name) {
		if (StringUtils.isBlank(name)) { // 默认返回矩形
			return RECT;
		}

		ImageStyle style = map.get(name.toUpperCase());
		return style == null ? RECT : style;
	}

	public abstract void draw(Graphics2D g2d, int x, int y, int size, BufferedImage img);
}
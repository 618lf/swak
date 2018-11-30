package com.tmt.manage.widgets.theme;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * 主题
 * 
 * @author lifeng
 */
public interface Theme {

	/**
	 * 名称
	 * 
	 * @return
	 */
	String name();

	/**
	 * 路径
	 * 
	 * @return
	 */
	String path();

	/**
	 * 操作
	 * 
	 * @return
	 */
	default List<Action> actions() {
		return null;
	};

	/**
	 * 操作
	 * 
	 * @author lifeng
	 */
	public static class Action {

		private String name;
		private Image image;
		private Image imageOn;
		private Runnable runnable;
		private Consumer<Object> accept;
		private Color color;
		private Color colorOn;

		public String name() {
			return name;
		}

		public Action name(String name) {
			this.name = name;
			return this;
		}
		
		public Color color() {
			return color;
		}
		
		public Color colorOn() {
			return colorOn;
		}

		public Action color(Color color) {
			this.color = color;
			return this;
		}
		
		public Action colorOn(Color color) {
			this.colorOn = color;
			return this;
		}

		public Image image() {
			return image;
		}

		public Action image(Image image) {
			this.image = image;
			return this;
		}

		public Image imageOn() {
			return imageOn;
		}

		public Action imageOn(Image imageOn) {
			this.imageOn = imageOn;
			return this;
		}

		public Runnable click() {
			return runnable;
		}

		public Action click(Runnable runnable) {
			this.runnable = runnable;
			return this;
		}
		
		public Consumer<Object> accept() {
			return accept;
		}
		public Action accept(Consumer<Object> accept) {
			this.accept = accept;
			return this;
		}

		/**
		 * 构造
		 * 
		 * @return
		 */
		public static Action me() {
			return new Action();
		}
	}
}

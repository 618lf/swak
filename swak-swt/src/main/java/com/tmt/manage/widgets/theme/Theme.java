package com.tmt.manage.widgets.theme;

import java.util.List;

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

		public String name() {
			return name;
		}

		public Action name(String name) {
			this.name = name;
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

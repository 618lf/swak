package com.swak.manage.widgets.theme.apple;

import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.function.Consumer;

import com.swak.manage.widgets.Theme;

/**
 * 苹果主题的设置
 * 
 * @author lifeng
 */
public abstract class AppleTheme implements Theme {

	@Override
	public String name() {
		return "Apple";
	}

	@Override
	public String path() {
		return "apple.AppleApp";
	}
	
	/**
	 * logo
	 * 
	 * @return
	 */
	public abstract Action logo();
	
	/**
	 * background
	 * 
	 * @return
	 */
	public abstract Action background();
	
	/**
	 * 操作
	 * 
	 * @return
	 */
	public abstract List<Action> actions();
	
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
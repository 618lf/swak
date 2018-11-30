package com.tmt.manage.widgets;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 统一控制状态
 * 
 * @author lifeng
 */
public class ImageButtonGroup {

	private Set<ImageButton> buttons = new LinkedHashSet<>(2);

	/**
	 * 添加受管理的组件
	 * 
	 * @param name
	 * @param button
	 */
	public void addImageButton(ImageButton button) {
		buttons.add(button);
	}

	/**
	 * 将指定button点亮，其他的关闭
	 * 
	 * @param button
	 */
	public void on(ImageButton button) {
		buttons.forEach((b) -> {
			if (b != button) {
				b.off();
			}
		});
		button.on();
	}
	
	/**
	 * 将指定button点亮，其他的关闭
	 * 
	 * @param button
	 */
	public void first() {
		buttons.iterator().next().on();
	}
}
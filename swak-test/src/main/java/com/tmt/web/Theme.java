package com.tmt.web;

/**
 * 测试主题
 * @author lifeng
 */
public class Theme {

	private Long id;
	private String name;
	private String code;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * 创建
	 * @return
	 */
	public static Theme newTheme() {
		Theme theme = new Theme();
		theme.setId(1L);
		theme.setName("lifeng");
		theme.setCode("134");
		return theme;
	}
}
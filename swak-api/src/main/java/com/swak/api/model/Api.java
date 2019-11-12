package com.swak.api.model;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Api 文档
 * 
 * @author lifeng
 */
@Data
@Accessors(chain = true)
public class Api {
	private String name;
	private String desc;
	private List<ApiMethod> methods;
}

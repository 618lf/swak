package com.swak.doc;

import java.util.List;

import com.swak.utils.Lists;

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

	public Api addApiMethod(ApiMethod method) {
		if (methods == null) {
			methods = Lists.newArrayList();
		}
		methods.add(method);
		return this;
	}
}

package com.swak.api.model;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * java api method info model.
 */
@Data
@Accessors(chain = true)
public class ApiMethod {

	private String name;
	private String desc;
	private List<String> urls;
	private String method;
	private String headers;
	private String contentType;
	private List<ApiHeader> requestHeaders;
	private List<ApiParam> requestParams;
	private String requestUsage;
	private String responseUsage;
	private List<ApiParam> responseParams;
}

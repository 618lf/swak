package com.swak.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Description: http request header info model
 *
 * @author yu 2018/06/18.
 */
@Data
@Accessors(chain = true)
public class ApiHeader {

	private String name;
	private String type;
	private String desc;
}

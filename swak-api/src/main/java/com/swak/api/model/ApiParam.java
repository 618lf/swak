package com.swak.api.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yu 2019/9/27.
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class ApiParam {

	private String field;
	private String type;
	private String desc;
	private boolean required;
	private String version;
}

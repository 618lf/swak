package com.swak.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yu 2019/9/22.
 * @since 1.7 +
 */
@Data
@Accessors(chain = true)
public class ApiReturn {

	private String genericCanonicalName;
	private String simpleName;
}

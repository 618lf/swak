package com.swak.doc;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * api return
 *
 * @author: lifeng
 * @date: 2020/3/29 11:02
 */
@Data
@Accessors(chain = true)
public class ApiReturn {

    private String genericCanonicalName;
    private String simpleName;
}

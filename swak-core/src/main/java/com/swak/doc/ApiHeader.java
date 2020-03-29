package com.swak.doc;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Description: http request header info model
 *
 * @author: lifeng
 * @date: 2020/3/29 11:02
 */
@Data
@Accessors(chain = true)
public class ApiHeader {

    private String name;
    private String type;
    private String desc;
}

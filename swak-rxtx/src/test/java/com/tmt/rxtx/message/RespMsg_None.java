package com.tmt.rxtx.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 空响应
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class RespMsg_None extends BaseMsg {

	private String message;
}

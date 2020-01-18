package com.tmt.rxtx.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 响应数据：系统是请求端，设备是响应端
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class RespMsg_Cmd extends BaseMsg{
}
package com.swak.validator;

import com.swak.annotation.Email;
import com.swak.annotation.Length;
import com.swak.annotation.Max;
import com.swak.annotation.Min;
import com.swak.annotation.NotNull;
import com.swak.annotation.Phone;
import com.swak.annotation.Regex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 参数测试
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class OrderParam {

	@NotNull
	@Length(min = 6, max = 20, msg="名称长度必须介于6～20之间")
	private String name;
	@Email
	private String email;
	@Phone
	private String phone;
	@Min(value = 10, msg="订单金额最小值为10元")
	@Max(value = 20, msg="订单金额最大值为20元")
	private Integer price;
	@Regex("\\w+")
	private String address;

}

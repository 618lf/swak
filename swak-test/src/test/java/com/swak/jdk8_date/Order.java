package com.swak.jdk8_date;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Order {

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;
}

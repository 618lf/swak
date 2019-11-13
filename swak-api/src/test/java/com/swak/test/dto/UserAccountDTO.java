package com.swak.test.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserAccountDTO {

	private String openId;
}

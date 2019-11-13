package com.swak.test.dto;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDTO {

	private Long id;
	private String name;
	private List<UserAccountDTO> accounts;
}

package com.swak.json;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StringOrder extends GenEntity<String, String> {

	private static final long serialVersionUID = 1L;
	private String name2;

}

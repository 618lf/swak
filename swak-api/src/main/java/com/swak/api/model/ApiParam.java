package com.swak.api.model;

import java.util.List;

import com.swak.utils.Lists;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yu 2019/9/27.
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class ApiParam {

	private String field;
	private String type;
	private String desc;
	private boolean json;
	private List<String> valids;

	public ApiParam addValid(String valid) {
		if (valids == null) {
			valids = Lists.newArrayList();
		}
		valids.add(valid);
		return this;
	}
}

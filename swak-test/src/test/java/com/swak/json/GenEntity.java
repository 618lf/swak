package com.swak.json;

import com.swak.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GenEntity<PK, PK2> extends BaseEntity<PK> {

	private static final long serialVersionUID = 1L;

	private PK2 pk2;
	private String pk3;
	
	public GenEntity<PK, PK2> setPk2(PK2 pk2) {
		this.pk2 = pk2;
		return  this;
	}

}

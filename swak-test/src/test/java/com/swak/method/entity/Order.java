package com.swak.method.entity;

import java.io.Serializable;

import com.swak.entity.IdEntity;

public class Order extends IdEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Long prePersist() {
		if (this.id == null) {
			return super.prePersist();
		}
		return this.id;
	}

}

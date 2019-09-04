package com.swak.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.swak.entity.IdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 商品管理
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Goods extends IdEntity<String> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private BigDecimal cost;
	private BigDecimal price;
	private String image;
	private String details;
	private Date day;
}
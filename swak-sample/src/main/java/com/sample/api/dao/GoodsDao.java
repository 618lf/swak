package com.sample.api.dao;

import org.springframework.stereotype.Repository;

import com.sample.api.entity.Goods;
import com.swak.persistence.BaseDaoImpl;

@Repository
public class GoodsDao extends BaseDaoImpl<Goods, String>{}

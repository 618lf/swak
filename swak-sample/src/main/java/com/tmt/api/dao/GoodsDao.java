package com.tmt.api.dao;

import org.springframework.stereotype.Repository;

import com.swak.persistence.BaseDaoImpl;
import com.tmt.api.entity.Goods;

@Repository
public class GoodsDao extends BaseDaoImpl<Goods, String>{}

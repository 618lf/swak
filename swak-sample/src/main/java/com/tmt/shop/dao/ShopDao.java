package com.tmt.shop.dao;

import org.springframework.stereotype.Repository;

import com.swak.persistence.BaseDaoImpl;
import com.tmt.shop.entity.Shop;

@Repository
public class ShopDao extends BaseDaoImpl<Shop, Long>{}

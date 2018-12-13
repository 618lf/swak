package com.tmt.shop.dao;

import org.springframework.stereotype.Repository;

import com.swak.persistence.BaseDaoImpl;
import com.tmt.shop.entity.Order;

@Repository
public class OrderDao extends BaseDaoImpl<Order, Long>{}

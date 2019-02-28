package com.tmt.gen.dao;

import org.springframework.stereotype.Repository;

import com.swak.persistence.BaseDaoImpl;
import com.tmt.gen.entity.Table;

@Repository
public class TableDao extends BaseDaoImpl<Table, Long>{}
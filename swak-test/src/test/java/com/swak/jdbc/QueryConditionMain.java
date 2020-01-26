package com.swak.jdbc;

import com.google.common.collect.Lists;
import com.swak.persistence.QueryCondition;
import com.swak.persistence.QueryCondition.Criteria;

public class QueryConditionMain {

	public static void main(String[] args) {
		QueryCondition qc = new QueryCondition();
		Criteria criteria = qc.getCriteria();
		criteria.andEqualTo("ID", "111");
		criteria.andEqualTo("NAME", "222");
		criteria.andBetween("NAME", "1", "2");
		criteria.andBetween("NAME", "1", 2);
		criteria.andConditionSql("11 = 11");
		criteria.andConditionSql("22 = 22");
		criteria.andIn("ID", Lists.newArrayList("1", "2"));
		System.out.println(qc);
	}
}

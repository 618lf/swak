package com.swak.persistence;

import java.util.Date;
import java.util.List;

import com.swak.utils.StringUtils;

/**
 * QueryCondition 的创建器
 * 
 * @author lifeng
 * @date 2020年10月23日 上午11:09:08
 */
public class Query {

	public static Query of() {
		return new Query();
	}

	private QueryCondition query;

	private Query() {
		this.query = new QueryCondition();
	}

	public Query andIsNull(String column) {
		if (StringUtils.isNotBlank(column)) {
			query.getCriteria().andIsNull(column);
		}
		return this;
	}

	public Query andIsNotNull(String column) {
		if (StringUtils.isNotBlank(column)) {
			query.getCriteria().andIsNotNull(column);
		}
		return this;
	}

	public Query andConditionSql(String conditionSql) {
		if (StringUtils.isNotBlank(conditionSql)) {
			query.getCriteria().andConditionSql(conditionSql);
		}
		return this;
	}

	public Query andEqualTo(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andEqualTo(column, value);
		}
		return this;
	}

	public Query andNotEqualTo(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andNotEqualTo(column, value);
		}
		return this;
	}

	public Query andGreaterThan(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andGreaterThan(column, value);
		}
		return this;
	}

	public Query andGreaterThanOrEqualTo(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andGreaterThanOrEqualTo(column, value);
		}
		return this;
	}

	public Query andLessThan(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andLessThan(column, value);
		}
		return this;
	}

	public Query andLessThanOrEqualTo(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andLessThanOrEqualTo(column, value);
		}
		return this;
	}

	public Query andIn(String column, List<?> values) {
		if (StringUtils.isNotBlank(column) && values != null) {
			query.getCriteria().andIn(column, values);
		}
		return this;
	}

	public Query andNotIn(String column, List<?> values) {
		if (StringUtils.isNotBlank(column) && values != null) {
			query.getCriteria().andNotIn(column, values);
		}
		return this;
	}

	public Query andBetween(String column, Object value1, Object value2) {
		if (StringUtils.isNotBlank(column) && value1 != null && value2 != null) {
			query.getCriteria().andBetween(column, value1, value2);
		}
		return this;
	}

	public Query andNotBetween(String column, Object value1, Object value2) {
		if (StringUtils.isNotBlank(column) && value1 != null && value2 != null) {
			query.getCriteria().andBetween(column, value1, value2);
		}
		return this;
	}

	public Query andLike(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andLike(column, value);
		}
		return this;
	}

	public Query andLeftLike(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andLeftLike(column, value);
		}
		return this;
	}

	public Query andRightLike(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andRightLike(column, value);
		}
		return this;
	}

	public Query andLikeColumn(String value, Object column) {
		if (StringUtils.isNotBlank(value) && column != null) {
			query.getCriteria().andLikeColumn(value, column);
		}
		return this;
	}

	public Query andNotLike(String column, Object value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andNotLike(column, value);
		}
		return this;
	}

	public Query andDateEqualTo(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateEqualTo(column, value);
		}
		return this;
	}

	public Query andDateNotEqualTo(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateNotEqualTo(column, value);
		}
		return this;
	}

	public Query andDateGreaterThan(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateGreaterThan(column, value);
		}
		return this;
	}

	public Query andDateGreaterThanOrEqualTo(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateGreaterThanOrEqualTo(column, value);
		}
		return this;
	}

	public Query andDateLessThan(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateLessThan(column, value);
		}
		return this;
	}

	public Query andDateLessThanOrEqualTo(String column, Date value) {
		if (StringUtils.isNotBlank(column) && value != null) {
			query.getCriteria().andDateLessThanOrEqualTo(column, value);
		}
		return this;
	}

	public Query andDateIn(String column, List<Date> values) {
		if (StringUtils.isNotBlank(column) && values != null) {
			query.getCriteria().andDateIn(column, values);
		}
		return this;
	}

	public Query andDateNotIn(String column, List<Date> values) {
		if (StringUtils.isNotBlank(column) && values != null) {
			query.getCriteria().andDateNotIn(column, values);
		}
		return this;
	}

	public Query andDateBetween(String column, Date value1, Date value2) {
		if (StringUtils.isNotBlank(column) && value1 != null && value2 != null) {
			query.getCriteria().andDateBetween(column, value1, value2);
		}
		return this;
	}

	public Query andDateNotBetween(String column, Date value1, Date value2) {
		if (StringUtils.isNotBlank(column) && value1 != null && value2 != null) {
			query.getCriteria().andDateNotBetween(column, value1, value2);
		}
		return this;
	}

	public Query setOrderByClause(String orderByClause) {
		if (StringUtils.isNotBlank(orderByClause)) {
			query.setOrderByClause(orderByClause);
		}
		return this;
	}

	public QueryCondition build() {
		return query;
	}
}

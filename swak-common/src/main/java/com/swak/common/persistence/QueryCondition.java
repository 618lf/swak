package com.swak.common.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.swak.common.utils.StringUtils;

/**
 * 查询条件构造类
 * @author root
 */
public class QueryCondition implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String orderByClause;
	protected Criteria criteria;

	public QueryCondition() {
		criteria = new Criteria();
	}
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	public String getOrderByClause() {
		return orderByClause;
	}
	public Criteria getCriteria() {
		return criteria;
	}
	public void clear() {
		criteria = new Criteria();
	}
	
	//---------------条件-----------------------
	public static class SingleValue {
		private String condition;
		private Object value;
		public String getCondition() {
			return condition;
		}
		public void setCondition(String condition) {
			this.condition = condition;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
	public static class BetweenValue {
		private String condition;
		private Object first;
		private Object second;
		public String getCondition() {
			return condition;
		}
		public void setCondition(String condition) {
			this.condition = condition;
		}
		public Object getFirst() {
			return first;
		}
		public void setFirst(Object first) {
			this.first = first;
		}
		public Object getSecond() {
			return second;
		}
		public void setSecond(Object second) {
			this.second = second;
		}
	}
	public static class MultiValue {
	    private String condition;
	    private List<?> values;
		public String getCondition() {
			return condition;
		}
		public void setCondition(String condition) {
			this.condition = condition;
		}
		public List<?> getValues() {
			return values;
		}
		public void setValues(List<?> values) {
			this.values = values;
		}
	}
	public static class Criteria implements Serializable{
		private static final long serialVersionUID = 1L;
		protected List<String> criteriaWithoutValue;
		protected List<SingleValue> criteriaWithSingleValue;
		protected List<MultiValue> criteriaWithListValue;
		protected List<BetweenValue> criteriaWithBetweenValue;

		protected Criteria() {
			criteriaWithoutValue = new ArrayList<String>();
			criteriaWithSingleValue = new ArrayList<SingleValue>();
			criteriaWithListValue = new ArrayList<MultiValue>();
			criteriaWithBetweenValue = new ArrayList<BetweenValue>();
		}

		public boolean isValid() {
			return criteriaWithoutValue.size() > 0 || criteriaWithSingleValue.size() > 0 || criteriaWithListValue.size() > 0|| criteriaWithBetweenValue.size() > 0;
		}

		public List<String> getCriteriaWithoutValue() {
			return criteriaWithoutValue;
		}

		public List<SingleValue> getCriteriaWithSingleValue() {
			return criteriaWithSingleValue;
		}

		public List<MultiValue> getCriteriaWithListValue() {
			return criteriaWithListValue;
		}

		public List<BetweenValue> getCriteriaWithBetweenValue() {
			return criteriaWithBetweenValue;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteriaWithoutValue.add(condition);
		}

		protected void addCriterion(String condition, Object value) {
			if (value == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			SingleValue map = new SingleValue();
			map.setCondition(condition);
			map.setValue(escape(value));
			criteriaWithSingleValue.add(map);
		}

		protected void addCriterion(String condition, List<?> values) {
			if (values == null || values.size() == 0) {
				throw new RuntimeException("Value list for  condition cannot be null or empty");
			}
			MultiValue map = new MultiValue();
			map.setCondition(condition);
			map.setValues(values);
			criteriaWithListValue.add(map);
		}

		protected void addCriterion(String condition, Object value1, Object value2) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for condition cannot be null");
			}
			BetweenValue values = new BetweenValue();
			values.setCondition(condition);
			values.setFirst(escape(value1));
			values.setSecond(escape(value2));
			criteriaWithBetweenValue.add(values);
		}

		protected void addCriterionForJDBCDate(String condition, Date value) {
			addCriterion(condition, new java.sql.Timestamp(value.getTime()));
		}

		protected void addCriterionForJDBCDate(String condition, List<Date> values) {
			if (values == null || values.size() == 0) {
				throw new RuntimeException("Value list for condition cannot be null or empty");
			}
			List<Date> dateList = new ArrayList<Date>();
			Iterator<Date> iter = values.iterator();
			while (iter.hasNext()) {
				dateList.add(new java.sql.Timestamp(((Date) iter.next()).getTime()));
			}
			addCriterion(condition, dateList);
		}

		protected void addCriterionForJDBCDate(String condition, Date value1, Date value2) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException(StringUtils.format("Between values for condition cannot be null"));
			}
			addCriterion(condition, new java.sql.Timestamp(value1.getTime()), new java.sql.Timestamp(value2.getTime()));
		}

		public Criteria andIsNull(String column) {
			addCriterion(new StringBuilder(column).append(" IS NULL").toString());
			return this;
		}

		public Criteria andIsNotNull(String column) {
			addCriterion(new StringBuilder(column).append(" IS NOT NULL").toString());
			return this;
		}
		
		public Criteria andConditionSql(String conditionSql) {
			addCriterion(conditionSql);
			return this;
		}

		public Criteria andEqualTo(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" =").toString(), value);
			return this;
		}

		public Criteria andNotEqualTo(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" <>").toString(), value);
			return this;
		}

		public Criteria andGreaterThan(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" >").toString(), value);
			return this;
		}

		public Criteria andGreaterThanOrEqualTo(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" >=").toString(), value);
			return this;
		}

		public Criteria andLessThan(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" <").toString(), value);
			return this;
		}

		public Criteria andLessThanOrEqualTo(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" <=").toString(), value);
			return this;
		}

		public Criteria andIn(String column, List<?> values) {
			addCriterion(new StringBuilder(column).append(" IN").toString(), values);
			return this;
		}

		public Criteria andNotIn(String column, List<?> values) {
			addCriterion(new StringBuilder(column).append(" NOT IN").toString(), values);
			return this;
		}

		public Criteria andBetween(String column, Object value1, Object value2) {
			addCriterion(new StringBuilder(column).append(" BETWEEN").toString(), value1, value2);
			return this;
		}

		public Criteria andNotBetween(String column, Object value1, Object value2) {
			addCriterion(new StringBuilder(column).append(" NOT BETWEEN").toString(), value1, value2);
			return this;
		}
		
        /**
         * 存储到 WithoutValue 中
         * 默认是 ','分开
         * @param column
         * @param property
         * @param value
         * @return
         */
		public Criteria andLike(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" LIKE CONCAT(CONCAT('%','").append(escape(value)).append("'),'%')").toString());
			return this;
		}
		
		/**
         * 存储到 WithoutValue 中
         * 默认是 ','分开
         * @param column
         * @param property
         * @param value
         * @return
         */
		public Criteria andLeftLike(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" LIKE CONCAT('%','").append(escape(value)).append("')").toString());
			return this;
		}
		
		/**
         * 存储到 WithoutValue 中
         * 默认是 ','分开
         * @param column
         * @param property
         * @param value
         * @return
         */
		public Criteria andRightLike(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" LIKE CONCAT('").append(escape(value)).append("', '%')").toString());
			return this;
		}
		
		/**
		 * values like column
		 * @param value
		 * @param property
		 * @param column
		 * @return
		 */
		public Criteria andLikeColumn(String value, Object column) {
			addCriterion(new StringBuilder("'").append(escape(value)).append("' LIKE CONCAT(CONCAT('%,', ").append(column).append("),',%')").toString());
			return this;
		}

		public Criteria andNotLike(String column, Object value) {
			addCriterion(new StringBuilder(column).append(" NOT LIKE CONCAT(CONCAT('%','").append(escape(value)).append("'),'%')").toString());
			return this;
		}

		//-----------------日期--------------------------------------
		public Criteria andDateEqualTo(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" =").toString(), value);
			return this;
		}

		public Criteria andDateNotEqualTo(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" <>").toString(), value);
			return this;
		}

		public Criteria andDateGreaterThan(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" >").toString(), value);
			return this;
		}

		public Criteria andDateGreaterThanOrEqualTo(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" >=").toString(), value);
			return this;
		}

		public Criteria andDateLessThan(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" <").toString(), value);
			return this;
		}

		public Criteria andDateLessThanOrEqualTo(String column, Date value) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" <=").toString(), value);
			return this;
		}

		public Criteria andDateIn(String column, List<Date> values) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" IN").toString(), values);
			return this;
		}

		public Criteria andDateNotIn(String column, List<Date> values) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" NOT IN").toString(), values);
			return this;
		}

		public Criteria andDateBetween(String column, Date value1, Date value2) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" BETWEEN").toString(), value1, value2);
			return this;
		}

		public Criteria andDateNotBetween(String column, Date value1, Date value2) {
			addCriterionForJDBCDate(new StringBuilder(column).append(" NOT BETWEEN").toString(), value1, value2);
			return this;
		}
		
		//转义数据库的特殊字符
		private Object escape(Object value) {
			if (value != null && value instanceof String ) {
				return StringUtils.escapeDb((String)value);
			}
			return value;
		}
	}
}
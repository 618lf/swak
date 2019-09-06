package com.swak.mongo.json;

import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * 更新操作
 * 
 * @author lifeng
 */
public class Update extends Document {

	private static final long serialVersionUID = 1L;

	protected void addMultiFieldOperation(String operator, String key, Object value) {
		Assert.hasText(key, "Key/Path for update must not be null or blank.");
		Object existingValue = this.get(operator);
		Document keyValueMap;
		if (existingValue == null) {
			keyValueMap = new Document();
			this.put(operator, keyValueMap);
		} else {
			if (existingValue instanceof Document) {
				keyValueMap = (Document) existingValue;
			} else {
				throw new RuntimeException(
						"Modifier Operations should be a LinkedHashMap but was " + existingValue.getClass());
			}
		}
		keyValueMap.put(key, value);
	}
	
	/**
	 * 修改
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Update set(Document doc) {
		this.put("$set", doc);
		return this;
	}

	/**
	 * 修改
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Update set(String key, Object value) {
		addMultiFieldOperation("$set", key, value);
		return this;
	}

	/**
	 * setOnInsert
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Update setOnInsert(String key, Object value) {
		addMultiFieldOperation("$setOnInsert", key, value);
		return this;
	}

	/**
	 * unset
	 * 
	 * @param key
	 * @return
	 */
	public Update unset(String key) {
		addMultiFieldOperation("$unset", key, 1);
		return this;
	}

	/**
	 * Update using the {@literal $inc} update modifier
	 * 
	 * @param key
	 * @param inc
	 * @return
	 */
	public Update inc(String key, Number inc) {
		addMultiFieldOperation("$inc", key, inc);
		return this;
	}

	public Update push(String key, Object value) {
		addMultiFieldOperation("$push", key, value);
		return this;
	}

	public Update addToSet(String key, Object value) {
		addMultiFieldOperation("$addToSet", key, value);
		return this;
	}

	public Update popFirst(String key) {
		addMultiFieldOperation("$pop", key, -1);
		return this;
	}

	public Update popLast(String key) {
		addMultiFieldOperation("$pop", key, 1);
		return this;
	}

	public Update pull(String key, Object value) {
		addMultiFieldOperation("$pull", key, value);
		return this;
	}

	public Update pullAll(String key, Object[] values) {
		addMultiFieldOperation("$pullAll", key, Arrays.asList(values));
		return this;
	}

	public Update rename(String oldName, String newName) {
		addMultiFieldOperation("$rename", oldName, newName);
		return this;
	}

	public Update currentDate(String key) {
		addMultiFieldOperation("$currentDate", key, true);
		return this;
	}

	public Update currentTimestamp(String key) {

		addMultiFieldOperation("$currentDate", key, new Document("$type", "timestamp"));
		return this;
	}

	public Update multiply(String key, Number multiplier) {

		Assert.notNull(multiplier, "Multiplier must not be null.");
		addMultiFieldOperation("$mul", key, multiplier.doubleValue());
		return this;
	}

	public Update max(String key, Object value) {

		Assert.notNull(value, "Value for max operation must not be null.");
		addMultiFieldOperation("$max", key, value);
		return this;
	}

	public Update min(String key, Object value) {

		Assert.notNull(value, "Value for min operation must not be null.");
		addMultiFieldOperation("$min", key, value);
		return this;
	}
}

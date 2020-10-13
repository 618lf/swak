package com.swak.async.persistence.define;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.util.ClassUtils;

import com.swak.annotation.Column;
import com.swak.annotation.Pk;
import com.swak.annotation.Table;
import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.entity.BaseEntity;
import com.swak.entity.IdEntity;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * 表定义
 * 
 * @author lifeng
 * @date 2020年10月8日 上午12:11:51
 */
public class TableDefine<T> {

	/**
	 * 类定义
	 */
	public ClassMeta meta;

	/**
	 * 对应的java 类型
	 */
	public Class<T> entity;

	/**
	 * 表定义
	 */
	public Table define;

	/**
	 * 表名称
	 */
	public String name;

	/**
	 * 主键
	 */
	public PkDefine pk;

	/**
	 * 所有列
	 */
	public List<ColumnDefine> columns;

	/**
	 * 创建表定义
	 * 
	 * @param entity
	 */
	public TableDefine(Class<T> entity) {

		// 实体定义
		this.entity = entity;
		this.define = entity.getAnnotation(Table.class);
		this.name = this.tableMapping();

		// 类型注册
		ClassMeta meta = FieldCache.set(entity, true);

		// 属性元数据
		Map<String, FieldMeta> fields = meta.getFields();

		// 循环处理
		// 不使用 BaseEntity、 IdEntity 中的字段
		fields.values().forEach(field -> {
			if (this.supportType(field) && this.ignored(field)) {
				this.addColumn(ColumnDefine.of(this.columnMapping(field), field.getPropertyName(), field));
				if (this.isPk(field)) {
					this.addPk(ColumnDefine.of(this.pkMapping(field), field.getPropertyName(), field));
				}
			}
		});

		// 检查PK -- 如果没有添加主键注解，则自动将ID列添加为主键
		this.addDefaultPk();
	}

	/**
	 * 添加列
	 * 
	 * @param column
	 * @return
	 */
	public TableDefine<T> addColumn(ColumnDefine column) {
		if (columns == null) {
			columns = Lists.newArrayList();
		}
		columns.add(column);
		return this;
	}

	/**
	 * 添加列
	 * 
	 * @param column
	 * @return
	 */
	public TableDefine<T> addPk(ColumnDefine column) {
		if (pk == null) {
			pk = new PkDefine();
		}
		if (pk.single == null) {
			pk.single = column;
		} else if (pk.columns == null) {
			pk.columns = Lists.newArrayList(2);
			pk.columns.add(pk.single);
			pk.columns.add(column);
		} else {
			pk.columns.add(column);
		}
		return this;
	}

	/**
	 * 添加默认的主键
	 * 
	 * @return
	 */
	public TableDefine<T> addDefaultPk() {
		if (this.pk != null) {
			return this;
		}

		// 必须有 id 属性
		if (this.columns != null) {
			for (ColumnDefine column : this.columns) {
				if (column.javaProperty.equals("id")) {
					this.addPk(column);
					break;
				}
			}
		}

		return this;
	}

	/**
	 * 是否识别到了列定义
	 * 
	 * @return
	 */
	public boolean hasColumn() {
		return this.columns != null && !this.columns.isEmpty();
	}

	/**
	 * 支持的类型，只支持基本的类型
	 * 
	 * @return
	 */
	protected boolean supportType(FieldMeta field) {
		Class<?> type = field.getFieldClass();
		return (ClassUtils.isPrimitiveOrWrapper(type) || Enum.class.isAssignableFrom(type)
				|| CharSequence.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type) || LocalDateTime.class.isAssignableFrom(type))
				&& !type.isArray() && !isNoUseBaseEntityField(field);
	}

	/**
	 * 是否是基类中的字段: 如果需要则在实体中重新定义字段
	 * 
	 * @param field
	 * @return
	 */
	protected boolean isNoUseBaseEntityField(FieldMeta field) {
		if (field.getField().getDeclaringClass() == BaseEntity.class
				|| field.getField().getDeclaringClass() == IdEntity.class) {
			return field.getField().getAnnotation(Column.class) == null;
		}
		return false;
	}

	/**
	 * 是否配置忽略
	 * 
	 * @param field
	 * @return
	 */
	protected boolean ignored(FieldMeta field) {
		Column column = field.getAnnotation(Column.class);
		return column == null || !column.ignore();
	}

	/**
	 * 是否配置忽略
	 * 
	 * @param field
	 * @return
	 */
	protected boolean isPk(FieldMeta field) {
		Pk column = field.getAnnotation(Pk.class);
		return column != null;
	}

	/**
	 * 列映射
	 * 
	 * @param meta
	 * @return
	 */
	protected String columnMapping(FieldMeta field) {
		Column column = field.getAnnotation(Column.class);
		if (column == null || StringUtils.isBlank(column.value())) {
			return StringUtils.convertProperty2Column(field.getPropertyName());
		}
		return column.value();
	}

	/**
	 * 列映射
	 * 
	 * @param meta
	 * @return
	 */
	protected String pkMapping(FieldMeta field) {
		Pk pk = field.getAnnotation(Pk.class);
		if (StringUtils.isBlank(pk.value())) {
			return pk.value();
		}
		return this.columnMapping(field);
	}

	/**
	 * 表名映射
	 * 
	 * @return
	 */
	protected String tableMapping() {
		Table table = this.entity.getAnnotation(Table.class);
		if (table == null || StringUtils.isBlank(table.value())) {
			return StringUtils.convertProperty2Column(StringUtils.lowerCaseFirstOne(this.entity.getSimpleName()));
		}
		return table.value();
	}

	/**
	 * 获得主键
	 * 
	 * @return
	 */
	public List<ColumnDefine> getPkColumns() {
		List<ColumnDefine> pks = Lists.newArrayList();
		if (this.pk != null && this.pk.columns != null) {
			for (ColumnDefine column : this.pk.columns) {
				pks.add(column);
			}
		} else if (this.pk != null) {
			ColumnDefine column = this.pk.single;
			pks.add(column);
		}
		return pks;
	}

	/**
	 * 得到属性值
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Object getFieldValue(T entity, String fieldName) throws IllegalArgumentException, IllegalAccessException {
		FieldMeta meta = this.meta.getFields().get(fieldName);
		if (meta == null) {
			return null;
		}
		return meta.getField().get(entity);
	}
}

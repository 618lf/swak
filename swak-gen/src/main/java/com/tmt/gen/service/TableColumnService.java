package com.tmt.gen.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.BaseDao;
import com.swak.service.BaseService;
import com.swak.utils.Maps;
import com.tmt.gen.dao.TableColumnDao;
import com.tmt.gen.entity.Table;
import com.tmt.gen.entity.TableColumn;
import com.tmt.gen.utils.GenUtils;
import com.tmt.gen.utils.Kits;

@Service
public class TableColumnService extends BaseService<TableColumn, Long>{

	@Autowired
	private TableColumnDao tableColumnDao;
	
	@Override
	protected BaseDao<TableColumn, Long> getBaseDao() {
		return tableColumnDao;
	}
	
	public Page queryDbPage(String name, String tableName, Parameters param) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("name", name);params.put("tableName", tableName);
		return this.queryForPageList("findDbTableColumns", params, param);
	}
	
	/**
	 * 查询物理表的列
	 * @param tableName
	 * @return
	 */
	public List<TableColumn> queryDbTableColumns(String tableName){
		Map<String,Object> params = Maps.newHashMap();
		params.put("tableName", tableName);
		List<TableColumn> columns = this.queryForList("findDbTableColumns", params);
		for(TableColumn column: columns) {
			column.setIsNull(column.getIsDbNull());
			column.setJavaField(Kits.convertColumn2Property(column.getName()));
			//设置java类型和jdbc类型
			GenUtils.convertDbType2JavaTypes(column);
			//设置默认的显示类型和校验类型
			GenUtils.convertDbType2ShowTypes(column);
		}
		return columns;
	}
	
	/**
	 * 查询配置表的列
	 * @param tableName
	 * @return
	 */
	public List<TableColumn> queryTableColumns(Long tableId){
		return this.queryForList("findTableColumns", tableId);
	}
	
	/**
	 * 保存配置
	 * @param column
	 */
	@Transactional
	public void save(Table table) {
		List<TableColumn> olds = this.queryTableColumns(table.getId());
		this.batchDelete(olds);
		this.batchInsert(table.getColumns());
	}
	
	/**
	 * 删除表字段配置
	 * @param column
	 */
	@Transactional
	public void delete(List<TableColumn> columns) {
		this.batchDelete(columns);
	}
}

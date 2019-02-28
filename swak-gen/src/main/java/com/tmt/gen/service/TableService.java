package com.tmt.gen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.incrementer.IdGen;
import com.swak.persistence.BaseDao;
import com.swak.service.BaseService;
import com.swak.utils.Lists;
import com.tmt.gen.dao.TableDao;
import com.tmt.gen.entity.Table;
import com.tmt.gen.entity.TableColumn;

@Service
public class TableService extends BaseService<Table, Long>{

	@Autowired
	private TableDao tableDao;
	@Autowired
	private TableColumnService tableColumnService;
	
	@Override
	protected BaseDao<Table, Long> getBaseDao() {
		return tableDao;
	}
	
	/**
	 * 得到表和列
	 * @param id
	 * @return
	 */
	public Table getWithColumns(Long id) {
		Table table = this.get(id);
		if(table != null) {
			List<TableColumn> dbColumns = tableColumnService.queryDbTableColumns(table.getName());
			List<TableColumn> columns = tableColumnService.queryTableColumns(id);
			if(columns != null && columns.size() != 0) {
				List<TableColumn> copys = Lists.newArrayList();
				for(TableColumn db : dbColumns) {
					Boolean bFound = Boolean.FALSE;
					for(TableColumn column : columns) {
						if(db.getName().equals(column.getName()) && db.getDbType().equals(column.getDbType())) {
							bFound = Boolean.TRUE; column.setIsPk(db.getIsPk());
							column.setIsDbNull(db.getIsDbNull());
							copys.add(column);
							break;
						}
					}
					if(!bFound) {
						db.setGenTableId(id);
						copys.add(db);
					}
				}
				dbColumns = copys;
			}
			//已数据库的列为准
			table.setColumns(dbColumns);
		}
		return table;
	}
	
	@Transactional
	public void save(Table table) {
		Boolean insert = Boolean.FALSE;
		if(IdGen.isInvalidId(table.getId())) {
			this.insert(table);
			insert = Boolean.TRUE;
		} else {
			this.update(table);
		}
		List<TableColumn> dbColumns = tableColumnService.queryDbTableColumns(table.getName());
		//保存列配置 -- 以实际的列为准
		if(!insert) {
			List<TableColumn> columns = table.getColumns();
			if(columns != null && columns.size() != 0) {
				List<TableColumn> copys = Lists.newArrayList();
				for(TableColumn db : dbColumns) {
					Boolean bFound = Boolean.FALSE;
					for(TableColumn column : columns) {
						if( db.getName().equals(column.getName()) && db.getDbType().equals(column.getDbType())) {
							bFound = Boolean.TRUE; column.setIsPk(db.getIsPk());
							if(Table.NO == db.getIsDbNull()) {
								column.setIsNull(db.getIsDbNull());
							}
							column.setLength(db.getLength());
							column.setScale(db.getScale());
							column.setJdbcType(db.getJdbcType());
							column.setJavaType(db.getJavaType());
							column.setJavaField(db.getJavaField());
							copys.add(column);
							break;
						}
					}
					if(!bFound) {
						db.setGenTableId(table.getId());
						copys.add(db);
					}
				}
				dbColumns = copys;
			}
		}
		table.setColumns(dbColumns);
		for(TableColumn column: dbColumns) {
			column.setGenTableId(table.getId());
			column.setTableName(table.getName());
		}
		//保存
		tableColumnService.save(table);
	}
	
	/**
	 * 查询物理表
	 * @param name
	 * @return
	 */
	public Table queryDbTableByTableName(String name) {
		List<Table> tables = this.queryDbTables(name);
		return tables.get(0);
	}
	
	/**
	 * 查询物理表
	 * @param name
	 * @return
	 */
	public List<Table> queryDbTables(String name) {
		List<Table> tables = this.queryForList("findDbTables", name);
		List<Table> _tables = Lists.newArrayList();
		if(tables != null && tables.size() != 0) {
			_tables.add(new Table());
			for(Table table: tables) {
				table.setComments(table.getName() + ":" +table.getComments());
				_tables.add(table);
			}
		}
		return _tables;
	}
	
	/**
	 * 删除表配置
	 */
	@Transactional
	public void delete(List<Table> tables) {
		List<TableColumn> columns = Lists.newArrayList();
		for(Table table: tables) {
			List<TableColumn> tableColumns = tableColumnService.queryTableColumns(table.getId());
			columns.addAll(tableColumns);
		}
		
		this.batchDelete(tables);
		this.tableColumnService.delete(columns);
	}
}

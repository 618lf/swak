package com.tmt.gen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.Constants;
import com.swak.incrementer.IdGen;
import com.swak.persistence.BaseDao;
import com.swak.service.BaseService;
import com.tmt.gen.dao.SchemeDao;
import com.tmt.gen.entity.Scheme;
import com.tmt.gen.entity.Table;
import com.tmt.gen.utils.GenUtils;

@Service
public class SchemeService extends BaseService<Scheme, Long> {

	@Autowired
	private SchemeDao schemeDao;
	@Autowired
	private TableService tableService;

	@Override
	protected BaseDao<Scheme, Long> getBaseDao() {
		return schemeDao;
	}

	public Scheme getWithTable(Long id) {
		Scheme scheme = this.get(id);
		if (scheme != null) {
			Table table = tableService.get(scheme.getGenTableId());
			if (table != null) {
				scheme.setGenTableName(table.getName());
			}
		}
		return scheme;
	}

	/**
	 * 保存
	 * 
	 * @param scheme
	 */
	@Transactional
	public void save(Scheme scheme) {
		if (IdGen.isInvalidId(scheme.getId())) {
			this.insert(scheme);
		} else {
			this.update(scheme);
		}
		// 生成代码
		if (Constants.YES == scheme.getFlag()) {
			this.gen(scheme);
		}
	}

	/**
	 * 生成代码
	 * 
	 * @param scheme
	 */
	@Transactional
	public void gen(Scheme scheme) {
		// 生成代码
		GenUtils.genCode(scheme);
	}

	/**
	 * 保存
	 * 
	 * @param scheme
	 */
	@Transactional
	public void delete(List<Scheme> schemes) {
		this.batchDelete(schemes);
	}
}

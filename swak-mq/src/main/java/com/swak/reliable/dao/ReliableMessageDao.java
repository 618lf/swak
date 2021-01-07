package com.swak.reliable.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.swak.persistence.BaseJdbcDao;
import com.swak.reliable.entity.ReliableMessageVO;

/**
 * 可靠消息服务
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:26:43
 */
@Repository
public class ReliableMessageDao extends BaseJdbcDao {

	/**
	 * 插入
	 * 
	 * @param id
	 * @return
	 */
	public void insert(ReliableMessageVO message) {
		message.prePersist();
		Map<String, Object> param = this.BeantoMap(message);
		this.insert(
				"INTO SYS_RELIABLE_MESSAGE(ID, NAME, GYRO, STARTTIME, ENDTIME, CJMS, CJPL, ODR, TOTAL, FRAMES, TIMES) VALUES(:id, :name, :gyro, :startTime, :endTime, :cjms, :cjpl, :odr, :total, :frames, :times)",
				param);
	}
}
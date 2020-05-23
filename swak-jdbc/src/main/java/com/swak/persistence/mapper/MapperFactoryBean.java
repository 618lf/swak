package com.swak.persistence.mapper;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;

/**
 * 代理创建具体的 Mapper
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:52:19
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {

	private SqlSessionTemplate sqlSessionTemplate;
	private Class<T> mapperInterface;

	public MapperFactoryBean() {
		// intentionally empty
	}

	public MapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	/**
	 * Sets the mapper interface of the MyBatis mapper
	 *
	 * @param mapperInterface class of the interface
	 */
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	/**
	 * Set the SqlSessionTemplate for this DAO explicitly, as an alternative to
	 * specifying a SqlSessionFactory.
	 *
	 * @param sqlSessionTemplate a template of SqlSession
	 * @see #setSqlSessionFactory
	 */
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getObject() throws Exception {
		return MapperProxy.newProxy(mapperInterface, sqlSessionTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {
		return this.mapperInterface;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}
}

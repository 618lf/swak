package com.swak.persistence.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.swak.entity.Parameters;
import com.swak.persistence.BaseDao;
import com.swak.persistence.BaseDaoImpl;
import com.swak.persistence.QueryCondition;

/**
 * Mapper 代理
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:52:11
 */
public class MapperProxy<T, PK> extends BaseDaoImpl<T, PK> implements InvocationHandler {

	/**
	 * Mapper 接口
	 */
	Class<T> mapperInterface;

	/**
	 * 需要设置 操作模板
	 * 
	 * @param sqlSessionTemplate
	 */
	public MapperProxy(Class<T> mapperInterface, SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate);
		this.mapperInterface = mapperInterface;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		BaseDao<T, PK> me = this;
		String methodName = method.getName();

		try {
			if ("lock".equals(methodName) && args.length == 1) {
				me.lock((T) args[0]);
				return null;
			}

			if ("get".equals(methodName) && args.length == 1) {
				return me.get((PK) args[0]);
			}

			if ("update".equals(methodName) && args.length == 2) {
				return new Integer(me.update((String) args[0], (T) args[1]));
			}

			if ("update".equals(methodName) && args.length == 1) {
				return new Integer(me.update((T) args[0]));
			}

			if ("delete".equals(methodName) && args.length == 2) {
				return new Integer(me.delete((String) args[0], (T) args[1]));
			}

			if ("delete".equals(methodName) && args.length == 1) {
				return new Integer(me.delete((T) args[0]));
			}

			if ("insert".equals(methodName) && args.length == 1) {
				return me.insert((T) args[0]);
			}

			if ("insert".equals(methodName) && args.length == 2) {
				return me.insert((String) args[0], (T) args[1]);
			}

			if ("exists".equals(methodName) && args.length == 1) {
				return new Boolean(me.exists((PK) args[0]));
			}

			if ("compareVersion".equals(methodName) && args.length == 1) {
				me.compareVersion((T) args[0]);
				return null;
			}

			if ("queryForLimitList".equals(methodName) && args.length == 3
					&& args[1].getClass().getName().equals("java.util.Map")) {
				return me.queryForLimitList((String) args[0], (Map<String, ?>) args[1], ((Number) args[2]).intValue());
			}

			if ("queryForLimitList".equals(methodName) && args.length == 3
					&& args[1].getClass().getName().equals("com.swak.persistence.QueryCondition")) {
				return me.queryForLimitList((String) args[0], (QueryCondition) args[1], ((Number) args[2]).intValue());
			}

			if ("queryForLimitList".equals(methodName) && args.length == 3) {
				return me.queryForLimitList((String) args[0], (Object) args[1], ((Number) args[2]).intValue());
			}

			if ("queryForLimitList".equals(methodName) && args.length == 2) {
				return me.queryForLimitList((QueryCondition) args[0], ((Number) args[1]).intValue());
			}

			if ("queryForGenericsList".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("java.util.Map")) {
				return me.queryForGenericsList((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("queryForGenericsList".equals(methodName) && args.length == 2) {
				return me.queryForGenericsList((String) args[0], (Object) args[1]);
			}

			if ("queryForMapPageList".equals(methodName) && args.length == 3) {
				return me.queryForMapPageList((String) args[0], (Map<String, ?>) args[1], (Parameters) args[2]);
			}

			if ("getAll".equals(methodName) && args.length == 0) {
				return me.getAll();
			}

			if ("batchInsert".equals(methodName) && args.length == 1) {
				me.batchInsert((List<T>) args[0]);
				return null;
			}

			if ("batchInsert".equals(methodName) && args.length == 2) {
				me.batchInsert((String) args[0], (List<T>) args[1]);
				return null;
			}

			if ("countByCondition".equals(methodName) && args.length == 1) {
				return me.countByCondition((QueryCondition) args[0]);
			}

			if ("countByCondition".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("com.swak.persistence.QueryCondition")) {
				return me.countByCondition((String) args[0], (QueryCondition) args[1]);
			}

			if ("countByCondition".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("java.util.Map")) {
				return me.countByCondition((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("countByCondition".equals(methodName) && args.length == 2) {
				return me.countByCondition((String) args[0], (Object) args[1]);
			}

			if ("queryByCondition".equals(methodName) && args.length == 1) {
				return me.queryByCondition((QueryCondition) args[0]);
			}

			if ("queryForList".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("java.util.Map")) {
				return me.queryForList((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("queryForList".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("com.swak.persistence.QueryCondition")) {
				return me.queryForList((String) args[0], (QueryCondition) args[1]);
			}

			if ("queryForList".equals(methodName) && args.length == 2) {
				return me.queryForList((String) args[0], (Object) args[1]);
			}

			if ("queryForList".equals(methodName) && args.length == 1) {
				return me.queryForList((String) args[0]);
			}

			if ("queryForMapList".equals(methodName) && args.length == 2) {
				return me.queryForMapList((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("queryForIdList".equals(methodName) && args.length == 2) {
				return me.queryForIdList((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("queryForObject".equals(methodName) && args.length == 1
					&& args[0].getClass().getName().equals("com.swak.persistence.QueryCondition")) {
				return me.queryForObject((QueryCondition) args[0]);
			}

			if ("queryForObject".equals(methodName) && args.length == 1
					&& args[0].getClass().getName().equals("java.lang.String")) {
				return me.queryForObject((String) args[0]);
			}

			if ("queryForObject".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("java.util.Map")) {
				return me.queryForObject((String) args[0], (Map<String, ?>) args[1]);
			}

			if ("queryForObject".equals(methodName) && args.length == 2) {
				return me.queryForObject((String) args[0], (Object) args[1]);
			}

			if ("queryForAttr".equals(methodName) && args.length == 2) {
				return me.queryForAttr((String) args[0], (Object) args[1]);
			}

			if ("queryForPage".equals(methodName) && args.length == 2) {
				return me.queryForPage((QueryCondition) args[0], (Parameters) args[1]);
			}

			if ("queryForPageList".equals(methodName) && args.length == 3
					&& args[1].getClass().getName().equals("com.swak.persistence.QueryCondition")
					&& args[2].getClass().getName().equals("com.swak.entity.Parameters")) {
				return me.queryForPageList((String) args[0], (QueryCondition) args[1], (Parameters) args[2]);
			}

			if ("queryForPageList".equals(methodName) && args.length == 3
					&& args[1].getClass().getName().equals("java.util.Map")
					&& args[2].getClass().getName().equals("com.swak.entity.Parameters")) {
				return me.queryForPageList((String) args[0], (Map<String, ?>) args[1], (Parameters) args[2]);
			}

			if ("batchUpdate".equals(methodName) && args.length == 1) {
				me.batchUpdate((List<T>) args[0]);
				return null;
			}

			if ("batchUpdate".equals(methodName) && args.length == 2
					&& args[1].getClass().getName().equals("java.util.List")) {
				me.batchUpdate((String) args[0], (List<T>) args[1]);
				return null;
			}

			if ("batchDelete".equals(methodName) && args.length == 1) {
				me.batchDelete((List<T>) args[0]);
				return null;
			}
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}

		throw new NoSuchMethodException("Not found method in class " + mapperInterface.getName());
	}

	/**
	 * 创建代理类
	 * 
	 * @param <T>
	 * @param mapperInterface
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T newProxy(Class<T> mapperInterface, SqlSessionTemplate sqlSessionTemplate) {
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface },
				new MapperProxy(mapperInterface, sqlSessionTemplate));
	}
}
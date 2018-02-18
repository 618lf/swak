package com.swak.security.context;

import java.util.Map;

import com.swak.common.utils.Maps;
import com.swak.security.exception.SecurityLifecycleException;
import com.swak.security.subjct.Subject;

/**
 * 不支持在子线程中使用, 子线程获取不到数据
 * 不支持在安全框架周期内使用，否则会抛出异常
 * @author lifeng
 */
public abstract class ThreadContext {

	public static final String SUBJECT_KEY = ThreadContext.class.getName()+ "_SUBJECT_KEY";
	private static final ThreadLocal<Map<String, Object>> resources = new ThreadLocal<Map<String, Object>>();

	protected ThreadContext() {}

	/**
	 * 不再安全框架的范畴内添加变量，不会成功,但也不会引起问题
	 * @param key
	 * @param value
	 */
	public static void put(String key, Object value) {
		put(key, value, false);
    }
	
	/**
	 * 添加属性
	 * @param key
	 * @param value
	 */
	private static void put(String key, Object value, boolean init) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            remove(key);
            return;
        }
        Map<String, Object> _r = resources.get();
        if (init && _r == null) {
        	_r = Maps.newHashMap();
        	resources.set(_r);
        } 
        
        // 这种情况直接抛出异常
        else if( _r == null){
        	throw new SecurityLifecycleException("超出了安全控件的生命周期");
        }
        _r.put(key, value);
    }
	
	/**
	 * 得到属性
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		Map<String, Object> perThreadResources = resources.get();
        return (T)(perThreadResources != null ? perThreadResources.get(key) : null);
    }
	
	/**
	 * 删除属性
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T remove(String key) {
        Map<String, Object> perThreadResources = resources.get();
        return (T)(perThreadResources != null ? perThreadResources.remove(key) : null);
    }
	
	/**
	 * 删除变量
	 */
	public static void remove() {
		if (resources.get() != null){
			resources.get().clear();
		}
		resources.remove();
	}
	
	// subject 操作
	public static void bind(Subject value) {
        if (value == null) {
            remove(SUBJECT_KEY);
            return;
        }
        put(SUBJECT_KEY, value, true);
    }
	public static Subject getSubject() {
        return resources.get() != null ? (Subject) resources.get().get(SUBJECT_KEY) : null;
    }
}
package com.swak.groovy;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.swak.cache.LRUCache;
import com.swak.codec.Digests;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * 表达式执行器 自定义脚本，可以继承Script --
 * http://www.open-open.com/lib/view/open1430744913304.html
 * 
 * @author root
 */
public abstract class ScriptExecutor {

	// 锁
	private final Object lock = new Object();

	// script 缓存 （最后能实现一个固定大小的MAP， 超过容量删除不常用的）
	private Map<String, Script> SCRIPT_CACHE = new LRUCache<String, Script>(100);

	// script 生成
	protected GroovyShell shell;

	/**
	 * 初始化
	 */
	public ScriptExecutor() {
		CompilerConfiguration cfg = new CompilerConfiguration();
		cfg.setScriptBaseClass(this.getBaseScriptClass());

		shell = new GroovyShell(cfg);
	}

	/**
	 * 基础脚本
	 * 
	 * @return
	 */
	protected abstract String getBaseScriptClass();

	/**
	 * 执行脚本
	 * 
	 * @param context    -- 上下文
	 * @param expression -- 表达式
	 * @return
	 */
	public Object execute(String expression, Map<String, Object> context) {
		Object scriptObject = null;
		try {

			// 设置参数
			Binding binding = new Binding();
			if (context != null && context.size() != 0) {
				Iterator<String> it = context.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					binding.setVariable(key, context.get(key));
				}
			}

			// shell 脚本
			Script shell = cached(expression);

			// 执行脚本
			scriptObject = (Object) InvokerHelper.createScript(shell.getClass(), binding).run();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return scriptObject;
	}

	/**
	 * 缓存脚本,并返回脚本
	 * 
	 * @param cacheKey
	 * @param shell
	 * @return
	 */
	private Script cached(String expression) {

		// MD5
		String cacheKey = Digests.md5(expression);

		// 缓存的脚本
		if (SCRIPT_CACHE.containsKey(cacheKey)) {
			return SCRIPT_CACHE.get(cacheKey);
		}

		// 重新创建
		synchronized (lock) {

			if (SCRIPT_CACHE.containsKey(cacheKey)) {
				return SCRIPT_CACHE.get(cacheKey);
			}

			Script script = shell.parse(expression);
			SCRIPT_CACHE.put(cacheKey, script);

			return script;
		}
	}
}
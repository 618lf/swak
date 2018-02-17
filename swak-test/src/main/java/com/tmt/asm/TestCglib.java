package com.tmt.asm;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TestCglib {

	public static void main(String[] args) {

	}
}

/**
 * cglib 来代理实现类
 * @author lifeng
 */
class CglibProxy implements MethodInterceptor {
	
	public Object object;

	public CglibProxy(Object object) {
		this.object = object;
	}
	
	/**
	 * 创建代理类
	 * @return
	 */
	public Object proxy() {
        // 创建加强器
        Enhancer enhancer = new Enhancer();
        // 设置需要加强的类
        enhancer.setSuperclass(object.getClass());
        // 设置回调
        enhancer.setCallback(this);
        enhancer.setClassLoader(object.getClass().getClassLoader());
        return enhancer.create();
    }
	
	@Override
    public Object intercept(Object arg0, Method arg1, Object[] arg2,
            MethodProxy arg3) throws Throwable {
        // 这里实现加强
        System.out.println("调用方法前处理方法");
        System.out.println("开始调用方法="+arg1);
        arg3.invoke(object, arg2);
        System.out.println("调用方法后的处理方法");
        return null;
    }
}

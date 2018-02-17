package com.tmt.asm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 如果只是接口没有实现类，则没有执行反射操作
 * 感觉这种方式更好一点。
 * @author lifeng
 */
public class TestJdkProxy {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		//生成$Proxy0的class文件
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        
        // --- 一般使用這種方式
        IHello ihello = (IHello) Proxy.newProxyInstance(IHello.class.getClassLoader(),  //加载接口的类加载器
                new Class[]{IHello.class},      //一组接口
                new HWInvocationHandler(new Hello())); //自定义的InvocationHandler
        ihello.sayHello();
	}
}

/**
 * 接口
 * @author lifeng
 *
 */
interface IHello{
    void sayHello();
}

/**
 * 接口实现类
 * @author lifeng
 *
 */
class Hello implements IHello{
    public void sayHello() {
        System.out.println("Hello world!!");
    }
}

/**
 * 代理拦截器
 * @author lifeng
 */
class HWInvocationHandler implements InvocationHandler{
    //目标对象
    private Object target;
    public HWInvocationHandler(Object target){
        this.target = target;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("------插入前置通知代码-------------");
        //执行相应的目标方法
        Object rs = method.invoke(target,args);
        System.out.println("------插入后置处理代码-------------");
        return rs;
    }
}
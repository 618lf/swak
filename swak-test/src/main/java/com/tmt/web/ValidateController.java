package com.tmt.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.swak.common.cache.Cache;
import com.swak.common.coroutines.TasksKt;
import com.swak.common.utils.JsonMapper;
import com.swak.http.HttpServletResponse;
import com.swak.http.Reportable;
import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.annotation.RequestMethod;
import com.tmt.cache.CacheUtils;

import kotlin.jvm.functions.Function1;

@Controller
@RequestMapping("/admin/validate")
public class ValidateController implements Reportable {

    private AtomicInteger count = new AtomicInteger();

    /**
     * 输出验证码
     *
     * @return
     */
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String postCode() {
    	Object o = TasksKt.taskLaunch(3000L, new Function1<Object, Object>() {
            public Object invoke(Object p) {
                System.out.println("异步打印的结果");
                return 1;
            }
        });
        System.out.println(o);
        return "1";
    }
    
    /**
     * 输出验证码
     *
     * @return
     */
    @RequestMapping(value = "/async")
    public void asyncCode(HttpServletResponse response) {
    	// 异步执行结果
    	TasksKt.taskBlockOnWorkThread(1000L, new Function1<Object, Object>() {
            public Object invoke(Object p) {
                System.out.println("异步打印的结果");
                return 1;
            }
        });
    	System.out.println("结果");
    	
    	// 数据
    	Theme theme = Theme.newTheme();
    	
    	// 存储到缓存
    	Cache<Object> cache = CacheUtils.sys().get();
    	cache.putString("ab", JsonMapper.toJson(theme));
    	response.out(JsonMapper.toJson(theme));
    }

    /**
     * 输出验证码
     *
     * @return
     */
    @RequestMapping(value = "/code")
    public String getCode() {
        return "和好";
    }

    /**
     * 打印
     */
    @Override
    public void report(MetricRegistry registry) {
        registry.register("Validate - count", (Gauge<Integer>) () -> count.get());
    }
}
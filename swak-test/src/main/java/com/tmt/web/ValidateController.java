package com.tmt.web;

import java.util.concurrent.atomic.AtomicInteger;

import com.swak.common.coroutines.TasksKt;
import kotlin.coroutines.experimental.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.experimental.Job;
import org.springframework.stereotype.Controller;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.swak.http.Reportable;
import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.annotation.RequestMethod;

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
        TasksKt.taskLaunch(3000L, new Function1<Object, Object>() {
            public Object invoke(Object p) {
                System.out.println("异步打印的结果");
                return 1;
            }
        });
        return "";
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
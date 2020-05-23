package com.swak.booter;

import com.swak.boot.Boot;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static com.swak.Application.APP_LOGGER;

/**
 * 系统启动后需要做的工作
 *
 * @author: lifeng
 * @date: 2020/4/1 12:24
 */
public class AppBooter implements ApplicationBooter {

    /**
     * 注意： <br>
     * 1. 如果是服务器环境则需要等待服务器启动之后再初始化其他组件<br>
     * 2. 如果是应用环境则可以直接启动<br>
     *
     * @param context application
     */
    @Override
    public void onApplicationEvent(ApplicationContext context) {
        String[] boots = context.getBeanNamesForType(Boot.class);
        if (boots.length > 0) {
            APP_LOGGER.debug("======== system startup loading ========");
            Arrays.stream(boots).forEach(s -> {
                Boot boot = context.getBean(s, Boot.class);
                APP_LOGGER.debug("Async loading - {}", boot.describe());
                boot.start();
            });
            APP_LOGGER.debug("======== system startup loaded  ========");
        }
    }
}
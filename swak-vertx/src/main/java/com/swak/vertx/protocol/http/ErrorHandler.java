package com.swak.vertx.protocol.http;

import com.swak.Constants;
import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.vertx.transport.HttpConst;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理一些异常信息
 *
 * @author: lifeng
 * @date: 2020/3/29 19:38
 */
public class ErrorHandler implements Handler<RoutingContext> {

    private Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    private int code;

    public ErrorHandler(int code) {
        this.code = code;
    }

    /**
     * 处理异常信息
     */
    @Override
    public void handle(RoutingContext context) {
        if (!context.response().ended() && !context.response().closed()) {
            if (code == Constants.INTERNAL_SERVER_ERROR.code()) {
                this.handle500(context);
            } else if (code == Constants.NOT_FOUND.code()) {
                this.handle400(context);
            }
        }
    }

    /**
     * 打印异常
     */
    private void handle500(RoutingContext context) {
        context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
        context.response().end(Result.error(ErrorCode.SERVER_ERROR).toJson());
        logger.error("{}", context.request().uri(), context.failure());
    }

    /**
     * 仅仅输出异常即可
     */
    private void handle400(RoutingContext context) {
        context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
        context.response().end(Result.error(ErrorCode.PATH_NOT_FOUNT).toJson());
    }
}

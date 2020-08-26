package com.swak.vertx.security.principal;

import java.util.concurrent.CompletionStage;

import com.swak.security.Subject;
import com.swak.security.Token;
import com.swak.vertx.security.Context;

/**
 * 身份解析
 *
 * @author: lifeng
 * @date: 2020/3/29 20:41
 */
public interface PrincipalStrategy {

    /**
     * 创建身份
     *
     * @param context 请求上下文
     * @return 异步Subject结果
     */
    CompletionStage<Subject> createSubject(Context context);

    /**
     * 生成 Token
     *
     * @param subject 请求上下文
     * @return 异步Token结果
     */
    CompletionStage<Token> generateToken(Subject subject);
}

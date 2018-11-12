package com.swak.reactivex.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.exception.ErrorCode;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.transport.http.server.ResponseStatusException;

import reactor.core.publisher.Mono;

/**
 * 默认的异常处理
 * 
 * @author lifeng
 */
public class DefaultWebExceptionHandler implements WebExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(DefaultWebExceptionHandler.class);
	private final String errorPage;

	public DefaultWebExceptionHandler() {
		errorPage = errorMessage();
	}

	/**
	 * 打印异常信息
	 */
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response, Throwable ex) {
		if (isSystemException(ex)) {
			ResponseStatusException statusException = (ResponseStatusException) ex;
			logger.error("{} - {}", request.getRequestURL(), ex.getMessage());
			response.status(statusException.getStatus()).json().accept().buffer(errorPage)
					.orJsonBuffer(ErrorCode.OPERATE_FAILURE.toJson());
		} else {
			logger.error("{}", request.getRequestURL(), ex);
			response.error().json().accept().buffer(errorPage).orJsonBuffer(ErrorCode.OPERATE_FAILURE.toJson());
		}
		return Mono.empty();
	}

	private boolean isSystemException(Throwable ex) {
		if (ex != null && ex instanceof ResponseStatusException) {
			return true;
		}
		return false;
	}

	public String errorMessage() {
		StringBuilder page = new StringBuilder();
		page.append("<!DOCTYPE html>");
		page.append("<html>");
		page.append("<head>");
		page.append("<title>Error</title>");
		page.append(
				"<style>body { width: 35em; margin: 0 auto;font-family: Tahoma, Verdana, Arial, sans-serif;}</style>");
		page.append("</head>");
		page.append("<body>");
		page.append("<h1>An error occurred.</h1>");
		page.append("<p>Sorry, the page you are looking for is currently unavailable.<br/>Please try again later.</p>");
		page.append("<p><em>Faithfully yours, swak.</em></p>");
		page.append("</body>");
		page.append("</html>");
		return page.toString();
	}
}
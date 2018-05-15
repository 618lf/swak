package com.swak.actuator.web;

import java.util.List;
import java.util.stream.Collectors;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.common.utils.Lists;
import com.swak.common.utils.StringUtils;
import com.swak.reactivex.web.DispatcherHandler;
import com.swak.reactivex.web.HandlerMapping;
import com.swak.reactivex.web.function.RouterFunction;
import com.swak.reactivex.web.function.RouterFunctionMapping;
import com.swak.reactivex.web.function.RouterFunctions.DefaultRouterFunction;
import com.swak.reactivex.web.method.AbstractRequestMappingHandlerMapping;
import com.swak.reactivex.web.method.HandlerMethod;
import com.swak.reactivex.web.method.RequestMappingInfo;

/**
 * 直接获取 dispatcherHandler 的内容
 * @author lifeng
 */
@Endpoint(id = "mappings")
public class MappingsEndpoint {
	
	private final DispatcherHandler dispatcherHandler;
	
	public MappingsEndpoint(DispatcherHandler dispatcherHandler) {
		this.dispatcherHandler = dispatcherHandler;
	}

	/**
	 * 所有的 mappings
	 * @return
	 */
	@Operation
	public HandlerMappings mappings() {
		List<String> handlers = Lists.newArrayList();
		handlers.addAll(dispatcherHandler.getMappings().stream().map(mapper ->{
			return requestMappingHandlersDescription(mapper);
		}).flatMap(hd -> hd.getHandlers().stream()).collect(Collectors.toList()));
		handlers.addAll(dispatcherHandler.getMappings().stream().map(mapper ->{
			return routerFunctionMapping(mapper);
		}).flatMap(hd -> hd.getHandlers().stream()).collect(Collectors.toList()));
		return new HandlerMappings(handlers);
	}
	
	private HandlersDescription requestMappingHandlersDescription(HandlerMapping mapping) {
		HandlersDescription hd = new HandlersDescription();
		if (mapping instanceof AbstractRequestMappingHandlerMapping) {
			((AbstractRequestMappingHandlerMapping)mapping).getMappingRegistry().getMappings().entrySet().stream().forEach(entry ->{
				RequestMappingInfo rm = entry.getKey();
				HandlerMethod hm = entry.getValue();
				hd.addHandler(String.format("(method %s && pattern %s -> function %s)", rm.getMethod().name(), StringUtils.join(rm.getPatterns(), ","), hm.getBeanType().getSimpleName() + "." + hm.getMethod().getName()));
			});
			
		}
		return hd;
	}
	
	private HandlersDescription routerFunctionMapping(HandlerMapping mapping) {
		HandlersDescription hd = new HandlersDescription();
		if (mapping instanceof RouterFunctionMapping) {
			RouterFunction routerFunction = ((RouterFunctionMapping)mapping).getRouterFunction();
			RouterFunction hf = routerFunction;
			while(hf != null) {
				if (hf instanceof DefaultRouterFunction) {
					hd.addHandler(hf.toString());
				}
				hf = routerFunction.next();
			}
		}
		return hd;
	}
	
	/**
	 * web mappings
	 * @author lifeng
	 */
	public static final class HandlerMappings {

		private final List<String> mappings;

		private HandlerMappings(List<String> mappings) {
			this.mappings = mappings;
		}

		public List<String> getMappings() {
			return mappings;
		}
	}
}
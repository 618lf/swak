package com.swak.actuator.web;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.flux.web.DispatcherHandler;
import com.swak.flux.web.HandlerMapping;
import com.swak.flux.web.function.RouterFunction;
import com.swak.flux.web.function.RouterFunctionMapping;
import com.swak.flux.web.function.RouterFunctions.ComposedRouterFunction;
import com.swak.flux.web.function.RouterFunctions.DefaultRouterFunction;
import com.swak.flux.web.method.AbstractRequestMappingHandlerMapping;
import com.swak.flux.web.method.HandlerMethod;
import com.swak.flux.web.method.RequestMappingInfo;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

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
				hd.addHandler(String.format("(method %s && pattern %s -> handler %s)", rm.getMethod().name(), StringUtils.join(rm.getPatterns(), ","), 
						hm.toString()));
			});
			
		}
		return hd;
	}
	
	private HandlersDescription routerFunctionMapping(HandlerMapping mapping) {
		HandlersDescription hd = new HandlersDescription();
		if (mapping instanceof RouterFunctionMapping) {
			RouterFunction routerFunction = ((RouterFunctionMapping)mapping).getRouterFunction();
			this.routerFunctionIteration(routerFunction, (rf) ->{
				hd.addHandler(rf.toString());
			});
		}
		return hd;
	}
	
	private void routerFunctionIteration(RouterFunction hf, Consumer<RouterFunction> consumer) {
		if (hf instanceof DefaultRouterFunction) {
			consumer.accept(hf);
		} else if(hf instanceof ComposedRouterFunction){
			ComposedRouterFunction chf = (ComposedRouterFunction)hf;
			routerFunctionIteration(chf.getFirst(), consumer);
			routerFunctionIteration(chf.getSecond(), consumer);
		}
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
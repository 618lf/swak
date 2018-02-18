package com.swak.security;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.mgt.FilterChainResolver;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.subjct.Subject;

/**
 * 安全过滤器
 * @author lifeng
 */
public class SecurityFilter implements Filter {

	private SecurityManager securityManager;
	private FilterChainResolver filterChainResolver;
	
	public SecurityFilter(SecurityManager securityManager, FilterChainResolver filterChainResolver) {
		this.securityManager = securityManager;
		this.filterChainResolver = filterChainResolver;
	}
	
	/**
	 * 具体的执行逻辑
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain){
		Throwable t = null;
		Subject subejct = null;
		try {
			
			subejct = securityManager.createSubject(request, response);
			subejct.execute(new Callable() {
				@Override
				public Object call() throws Exception {
					executeChain(request, response, filterChain);
					return null;
				}
			});
			
		} catch (Throwable throwable) {
            t = throwable;
        }
		
		if (t != null) {
            throw new RuntimeException("Filtered request failed.", t);
        }
	}
	
	/**
	 * 调用执行链
	 * @param request
	 * @param response
	 * @param origChain
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void executeChain(HttpServletRequest request, HttpServletResponse response, FilterChain origChain){
		FilterChain chain = getExecutionChain(request, response, origChain);
		chain.doFilter(request, response);
	}
	
	/**
	 * 得到执行链
	 * @param request
	 * @param response
	 * @param origChain
	 * @return
	 */
	protected FilterChain getExecutionChain(HttpServletRequest request, HttpServletResponse response, FilterChain origChain) {
		FilterChain chain = origChain;
		
		FilterChainResolver resolver = getFilterChainResolver();
        if (resolver == null) {
            return origChain;
        }
        
        FilterChain resolved = resolver.getChain(request, response, origChain);
        
        if (resolved != null) {
            chain = resolved;
        }

        return chain;
	}
	
	public FilterChainResolver getFilterChainResolver() {
        return filterChainResolver;
    }
    public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
        this.filterChainResolver = filterChainResolver;
    }
}
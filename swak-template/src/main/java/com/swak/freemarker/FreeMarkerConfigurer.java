package com.swak.freemarker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * 简单的
 * 
 * @author lifeng
 */
public class FreeMarkerConfigurer extends FreeMarkerConfigurationFactory
		implements ResourceLoaderAware, InitializingBean {

	private Configuration configuration;
	private Map<String, TemplateModel> tags;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.configuration == null) {
			this.configuration = createConfiguration();
		}

		if (tags != null) {
			Iterator<String> it = tags.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				TemplateModel value = tags.get(key);
				this.configuration.setSharedVariable(key, value);
			}
		}
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
		logger.info("ClassTemplateLoader for Spring macros added to FreeMarker configuration");
	}

	public void setTags(Map<String, TemplateModel> tags) {
		this.tags = tags;
	}
}
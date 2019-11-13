package com.swak.api;

import java.util.List;

import com.swak.api.builder.ApiBuilder;
import com.swak.api.model.Api;
import com.swak.utils.Lists;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 配置项目
 * 
 * @author lifeng
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class ApiConfig {

	private List<String> sourcePaths;
	private List<String> packages;
	
	/**
	 * 创建 Api Doc
	 * 
	 * @return
	 */
	public List<Api> build(){
		return new ApiBuilder(this).build();
	}
	
	/**
	 * 添加源码路径
	 * 
	 * @param sourcePath
	 * @return
	 */
	public ApiConfig addSourcePath(String sourcePath) {
		if (sourcePaths == null) {
			sourcePaths = Lists.newArrayList();
		}
		sourcePaths.add(sourcePath);
		return this;
	}

	/**
	 * 是否符合路径
	 * 
	 * @param javaFile
	 * @return
	 */
	public boolean isPackageMatch(String javaFile) {
		if (packages != null && packages.size() > 0) {
			for (String str : packages) {
				if (str.endsWith("*")) {
					String name = str.substring(0, str.length() - 2);
					if (javaFile.contains(name)) {
						return true;
					}
				} else {
					if (javaFile.contains(str)) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}
}

package com.swak.api;

import com.swak.api.builder.ApiBuilder;
import com.swak.doc.Api;
import com.swak.utils.Lists;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 配置项目
 *
 * @author: lifeng
 * @date: 2020/3/28 15:11
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
     * @return apis
     */
    public List<Api> build() {
        return new ApiBuilder(this).build();
    }

    /**
     * 添加源码路径
     *
     * @param sourcePath 源码文件路径
     * @return 配置
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
     * @param javaFile java文件
     * @return 是否符合包路径规则
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

package com.swak.entity;

import java.io.Serializable;
import java.util.Map;

import com.swak.utils.Maps;

/**
 * 模型
 *
 * @author lifeng
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    private String view;
    private Map<String, Object> context = Maps.newHashMap();

    public String getView() {
        return view;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Model view(String view) {
        this.view = view;
        return this;
    }

    public Model addAttribute(String name, Object value) {
        context.put(name, value);
        return this;
    }

    /**
     * 使用此模板创建一个 模型
     *
     * @param view 视图文件
     * @return 模型数据
     */
    public static Model use(String view) {
        Model model = new Model();
        return model.view(view);
    }
}
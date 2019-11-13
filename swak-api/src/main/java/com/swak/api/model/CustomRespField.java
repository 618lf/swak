package com.swak.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomRespField {

    private String name;
    private String desc;
    private String ownerClassName;
    private Object value;
}

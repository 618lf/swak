package com.swak.doc;

import java.util.List;

import com.swak.utils.Lists;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * api param
 *
 * @author: lifeng
 * @date: 2020/3/29 11:02
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class ApiParam {

    private String field;
    private String type;
    private String desc;
    private boolean json;
    private List<String> valids;
    private String value;

    public ApiParam addValid(String valid) {
        if (valids == null) {
            valids = Lists.newArrayList();
        }
        valids.add(valid);
        return this;
    }
}

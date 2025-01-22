package com.easyads.management.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Direction {
    private String property;
    private List<String> value;

    public Direction(String property, List<String> value) {
        // 这里做了一个很tricky的处理，对于没有定向的信息，我们会把property名字置为空，value置为空list
        if(CollectionUtils.isEmpty(value)) {
            this.property = StringUtils.EMPTY;
            this.value = new ArrayList<>();
        } else {
            this.property = property;
            this.value = value;
        }
    }
}

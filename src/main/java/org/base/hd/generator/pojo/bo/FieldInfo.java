package org.base.hd.generator.pojo.bo;

import lombok.Data;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 13:45
 */
@Data
public class FieldInfo {
    private String columnName;
    private String fieldName;
    private String fieldClass;
    private String fieldComment;
}

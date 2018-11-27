package org.base.hd.generator.pojo.bo;

import java.util.List;
import lombok.Data;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 13:45
 */
@Data
public class ClassInfo {

    private String tableName;
    private String className;
    private String classComment;

    private List<FieldInfo> fieldList;
}

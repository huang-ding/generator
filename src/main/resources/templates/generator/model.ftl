import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
* @description ${classInfo.classComment}
* @author huangding
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Data
public class ${classInfo.className}  {


<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
  <#list classInfo.fieldList as fieldItem >
    /**
    * ${fieldItem.fieldComment}
    */
    <#if (fieldItem.fieldName =="id")>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Column(name = "${fieldItem.columnName}")
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

  </#list>
</#if>

}
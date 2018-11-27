package org.base.hd.generator.controller;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.base.hd.generator.pojo.bo.ClassInfo;
import org.base.hd.generator.pojo.bo.ReturnT;
import org.base.hd.generator.util.FreemarkerUtil;
import org.base.hd.generator.util.TableParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 14:26
 */
@Controller
@Slf4j
public class IndexController {

    @Autowired
    private FreemarkerUtil freemarkerUtil;


    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/codeGenerate")
    @ResponseBody
    public ReturnT<Map<String, String>> codeGenerate(String tableSql) {

        try {
            if (StringUtils.isBlank(tableSql)) {
                return new ReturnT<Map<String, String>>(ReturnT.FAIL_CODE, "表结构信息不可为空");
            }

            // parse table
            ClassInfo classInfo = TableParseUtil.processTableIntoClassInfo(tableSql);

            // code genarete
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("classInfo", classInfo);

            // result
            Map<String, String> result = new HashMap<String, String>(1);

            result.put("controller_code", freemarkerUtil.processString("generator/controller.ftl", params));
            result.put("service_code", freemarkerUtil.processString("generator/service.ftl", params));
            result.put("dao_code", freemarkerUtil.processString("generator/dao.ftl", params));
            result.put("model_code", freemarkerUtil.processString("generator/model.ftl", params));

            // 计算,生成代码行数
            int lineNum = 0;
            for (Map.Entry<String, String> item : result.entrySet()) {
                if (item.getValue() != null) {
                    lineNum += StringUtils.countMatches(item.getValue(), "\n");
                }
            }
            log.info("生成代码行数：{}", lineNum);

            return new ReturnT<Map<String, String>>(result);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
            return new ReturnT<Map<String, String>>(ReturnT.FAIL_CODE, "表结构解析失败");
        }

    }


}

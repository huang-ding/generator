package org.base.hd.generator.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.base.hd.generator.pojo.bo.ClassInfo;
import org.base.hd.generator.pojo.bo.FieldInfo;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 13:48
 */
public class TableParseUtil {

    private static final String BACK_QUOTE = "`";
    private static final String UNDERLINE = "_";
    private static final String COMMENT_PATTERN = "\\ COMMENT '(.*?)\\'";


    /**
     * 解析建表SQL
     */
    public static ClassInfo processTableIntoClassInfo(String tableSql) {
        if (tableSql == null || tableSql.trim().length() == 0) {
            throw new RuntimeException("请输入相关sql语句");
        }
        tableSql = tableSql.trim();

        // 获取表名
        String tableName = null;
        String isTableStr = "TABLE";
        if (tableSql.contains(isTableStr) && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf(isTableStr) + 5, tableSql.indexOf("("));

        } else if (tableName.contains(isTableStr.toLowerCase()) && tableSql.contains("(")) {
            tableName = tableSql
                .substring(tableSql.indexOf(isTableStr.toLowerCase()) + 5, tableSql.indexOf("("));
        } else {
            throw new RuntimeException("sql语句错误");
        }

        if (tableName.contains(BACK_QUOTE)) {
            tableName = tableName
                .substring(tableName.indexOf(BACK_QUOTE) + 1, tableName.lastIndexOf(BACK_QUOTE));
        }

        // 获取类名
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains(" ")) {
            className = className.replaceAll(UNDERLINE, "");
        }

        // 获取 数据库注释
        String classComment = null;
        if (tableSql.contains("COMMENT=")) {
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("COMMENT=") + 8)
                .trim();
            if (classCommentTmp.contains("'") || classCommentTmp.indexOf("'") != classCommentTmp
                .lastIndexOf("'")) {

                classCommentTmp = classCommentTmp
                    .substring(classCommentTmp.indexOf("'") + 1, classCommentTmp.indexOf("'", 2));
            }
            if (classCommentTmp != null && classCommentTmp.trim().length() > 0) {
                classComment = classCommentTmp;
            }
        }

        List<FieldInfo> fieldList = new ArrayList<>();

        String fieldListTmp = tableSql
            .substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")")).trim();

        //转化使用英文逗号的注释
        Matcher matcher = Pattern.compile(COMMENT_PATTERN)
            .matcher(fieldListTmp);
        while (matcher.find()) {

            String commentTmp = matcher.group();

            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher.group(), commentTmpFinal);
            }
        }

        String[] fieldLineList = fieldListTmp.split(",");
        if (fieldLineList.length > 0) {
            for (String columnLine : fieldLineList) {

                columnLine = columnLine.trim();
                if (columnLine.startsWith(BACK_QUOTE)) {

                    columnLine = columnLine.substring(1);
                    String columnName = columnLine.substring(0, columnLine.indexOf(BACK_QUOTE));

                    // 字段名
                    String fieldName = StringUtils
                        .lowerCaseFirst(StringUtils.underlineToCamelCase(columnName));
                    if (fieldName.contains("_")) {
                        fieldName = fieldName
                            .replaceAll(UNDERLINE, org.apache.commons.lang3.StringUtils.EMPTY);
                    }

                    //字段类型
                    columnLine = columnLine.substring(columnLine.indexOf(BACK_QUOTE) + 1).trim();
                    String fieldClass = Object.class.getSimpleName();
                    String columnLineLowerCase = columnLine.toLowerCase();

                    if (columnLineLowerCase.startsWith("int") || columnLineLowerCase
                        .startsWith("tinyint") || columnLineLowerCase.startsWith("smallint")) {
                        fieldClass = Integer.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("bigint")) {
                        fieldClass = Long.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("float")) {
                        fieldClass = Float.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("double")) {
                        fieldClass = Double.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("datetime") || columnLineLowerCase
                        .startsWith("timestamp")) {
                        fieldClass = LocalDateTime.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("date")) {
                        fieldClass = LocalDate.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("time")) {
                        fieldClass = LocalTime.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("varchar") || columnLineLowerCase
                        .startsWith("text") || columnLineLowerCase.startsWith("tinytext")
                        || columnLineLowerCase.startsWith("mediumtext") || columnLineLowerCase
                        .startsWith("longtext")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("decimal")) {
                        fieldClass = BigDecimal.class.getSimpleName();
                    } else if (columnLineLowerCase.startsWith("char")) {
                        fieldClass = Character.class.getSimpleName();
                    }

                    // 字段注释
                    String fieldComment = null;
                    if (columnLine.contains("COMMENT")) {
                        // '用户ID',
                        String commentTmp = fieldComment = columnLine
                            .substring(columnLine.indexOf("COMMENT") + 7).trim();
                        if (commentTmp.contains("'") || commentTmp.indexOf("'") != commentTmp
                            .lastIndexOf("'")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("'") + 1,
                                commentTmp.lastIndexOf("'"));
                        }
                        fieldComment = commentTmp;
                    }

                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setFieldComment(fieldComment != null ? fieldComment
                        : org.apache.commons.lang3.StringUtils.EMPTY);

                    fieldList.add(fieldInfo);
                }
            }
        }

        if (fieldList.size() < 1) {
            throw new RuntimeException("该sql无字段");
        }

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment != null ? classComment
            : org.apache.commons.lang3.StringUtils.EMPTY);
        codeJavaInfo.setFieldList(fieldList);

        return codeJavaInfo;
    }

    public static void main(String[] args) throws IOException {
        String sql = "CREATE TABLE `we_chat_QR_code_info` (\n"
            + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
            + "\t`open_id` VARCHAR(50) NOT NULL,\n"
            + "\t`type` INT(11) NOT NULL COMMENT '活动二维码',\n"
            + "\t`event_key` INT(11) NOT NULL COMMENT '微信二维码场景Id',\n"
            + "\t`expire_seconds` INT(11) NULL DEFAULT NULL COMMENT '二维码的有效时间,单位为秒,空代表永久',\n"
            + "\t`url` VARCHAR(200) NOT NULL COMMENT '二维码地址',\n"
            + "\t`creator_time` DATETIME NOT NULL,\n"
            + "\tPRIMARY KEY (`id`),\n"
            + "\tINDEX `openId` (`open_id`)\n"
            + ")\n"
            + "COMMENT='微信二维码'\n"
            + "COLLATE='utf8_general_ci'\n"
            + "ENGINE=InnoDB\n"
            + ";\n";

        ClassInfo classInfo = processTableIntoClassInfo(sql);
        System.out.println(classInfo);
    }
}

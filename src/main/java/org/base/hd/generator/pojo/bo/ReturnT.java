package org.base.hd.generator.pojo.bo;

import lombok.Data;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 14:33
 */
@Data
public class ReturnT<T> {

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;
    public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
    public static final ReturnT<String> FAIL = new ReturnT<String>(FAIL_CODE, null);

    private int code;
    private String msg;
    private T data;

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnT(T data) {
        this.code = SUCCESS_CODE;
        this.data = data;
    }
}

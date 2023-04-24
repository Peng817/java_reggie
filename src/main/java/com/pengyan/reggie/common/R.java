package com.pengyan.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用结果类
 * @author 彭琰
 */
@Data
public class R<T> implements Serializable {

    /**
     * 编码
     * 1成果，否则失败
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 动态数据
     */
    private Map map = new HashMap();

    public static<T> R<T> success(T obj) {
        R<T> r = new R<T>();
        r.data = obj;
        r.code = 1;
        return r;
    }

    public static<T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key,Object value) {
        this.map.put(key,value);
        return this;
    }
}

package com.neusoft.yyzx.utils;

import lombok.Data;

/**
 * @author: wudagai
 */
@Data
public class ResultVo<T> {
    private boolean flag;
    private String message;
    private T data;

    /**
     * 成功不添加data
     *
     * @return
     */
    public static ResultVo ok(String message) {
        ResultVo resultVo = new ResultVo();
        resultVo.setMessage(message);
        resultVo.setFlag(true);
        return resultVo;
    }

    /**
     * 成功添加data
     *
     * @return
     */
    public static <T> ResultVo ok(T data) {
        ResultVo resultVo = new ResultVo();
        resultVo.setData(data);
        resultVo.setFlag(true);
        return resultVo;
    }

    /**
     * 成功添加data和message
     *
     * @return
     */
    public static <T> ResultVo ok(T data, String message) {
        ResultVo resultVo = new ResultVo();
        resultVo.setData(data);
        resultVo.setFlag(true);
        resultVo.setMessage(message);
        return resultVo;
    }

    /**
     * 失败
     *
     * @param message
     * @return
     */
    public static ResultVo fail(String message) {
        ResultVo resultVo = new ResultVo();
        resultVo.setFlag(false);
        resultVo.setMessage(message);
        return resultVo;
    }

    /**
     * 失败返回状态数据
     *
     * @param message
     * @return
     */
    public static <T> ResultVo fail(String message, T data) {
        ResultVo resultVo = new ResultVo();
        resultVo.setFlag(false);
        resultVo.setMessage(message);
        resultVo.setData(data);
        return resultVo;
    }

    /**
     * 异常
     *
     * @param e
     * @return
     */
    public static ResultVo error(Exception e) {
        ResultVo resultVo = new ResultVo();
        resultVo.setMessage("系统异常:" + e.getMessage());
        resultVo.setFlag(false);
        return resultVo;
    }
}
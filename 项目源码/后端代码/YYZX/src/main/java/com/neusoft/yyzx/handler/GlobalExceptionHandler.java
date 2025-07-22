package com.neusoft.yyzx.handler;

import com.neusoft.yyzx.utils.ResultVo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SignatureException.class)
    @ResponseBody
    public ResultVo handleSignatureException(SignatureException e) {
        return ResultVo.fail("token的非法操作，请自重", "token_error");
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseBody
    public ResultVo handleMalformedJwtException(MalformedJwtException e) {
        return ResultVo.fail("token解析异常", "token_error");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseBody
    public ResultVo handleExpiredJwtException(ExpiredJwtException e) {
        return ResultVo.fail("登录超时，请重新登录", "token_error");
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultVo handleNullPointerException(NullPointerException e) {
        logger.error("发生空指针异常", e);
        return ResultVo.fail("系统内部错误: 空指针异常", "system_error");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVo handleException(Exception e) {
        logger.error("系统异常", e);
        String message = e.getMessage();
        if (message != null && message.contains("token")) {
            return ResultVo.fail(message, "token_error");
        }
        return ResultVo.fail(message != null ? message : "系统发生未知错误");
    }
}
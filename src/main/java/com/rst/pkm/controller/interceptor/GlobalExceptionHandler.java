package com.rst.pkm.controller.interceptor;

import com.rst.pkm.common.Error;
import com.rst.pkm.dto.response.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hujia
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public CommonResult<String> exceptionHandler(RuntimeException e){
        logger.error("exceptionHandler {}", e);
        return CommonResult.make(Error.SERVER_EXCEPTION);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public CommonResult<String> exceptionHandler(CustomException e){
        logger.error("CustomException:{}", e.getError());
        return CommonResult.make(e.getError());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public CommonResult<String> exceptionHandler(HttpMessageNotReadableException e){
        logger.error("exceptionHandler {}",e);
        return CommonResult.make(Error.HTTP_CONTENT_INVALID);
    }
}

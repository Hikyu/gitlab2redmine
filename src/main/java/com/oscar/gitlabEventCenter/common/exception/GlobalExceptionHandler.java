/**
 * @Title: GlobalExceptionHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.exception
 * @author: Yukai  
 * @date: 2018年5月17日 上午10:16:48
 */
package com.oscar.gitlabEventCenter.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @ClassName: GlobalExceptionHandler
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 上午10:16:48
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalEventTypeException.class)
    public void undefinedEventTypeError(IllegalEventTypeException e) {
        logger.error("IllegalEventTypeException: {}", e.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    public void serverError(RuntimeException e) {
        logger.error("Server internal error: [{}]", e.getMessage(), e);
    }
}

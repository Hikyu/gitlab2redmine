/**
 * @Title: IllegalRequestException.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.exception
 * @author: Yukai  
 * @date: 2018年5月28日 上午11:04:17
 */
package com.oscar.gitlabEventCenter.common.exception;

/**
 * @ClassName: IllegalRequestException
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月28日 上午11:04:17
 */
public class IllegalRequestException extends RuntimeException {

    public IllegalRequestException(String msg) {
        super(msg);
    }

    public IllegalRequestException(String msg, Throwable e) {
        super(msg, e);
    }
}

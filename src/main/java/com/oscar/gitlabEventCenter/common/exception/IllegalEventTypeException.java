/**
 * @Title: IllegalEventTypeException.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.exception
 * @author: Yukai  
 * @date: 2018年5月17日 上午10:13:45
 */
package com.oscar.gitlabEventCenter.common.exception;

/**
 * @ClassName: IllegalEventTypeException
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 上午10:13:45
 */
public class IllegalEventTypeException extends RuntimeException{
    public IllegalEventTypeException(String msg) {
        super(msg);
    }
}

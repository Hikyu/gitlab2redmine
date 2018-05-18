/**
 * @Title: HttpRequestFailed.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.exception
 * @author: Yukai  
 * @date: 2018年5月17日 下午5:49:39
 */
package com.oscar.gitlabEventCenter.common.exception;

/**
 * @ClassName: HttpRequestFailed
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 下午5:49:39
 */
public class HttpRequestFailed extends RuntimeException {
    public HttpRequestFailed(String msg) {
        super(msg);
    }

    public HttpRequestFailed(String msg, Throwable e) {
        super(msg, e);
    }
}

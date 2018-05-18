/**
 * @Title: BaseEventHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service.handler
 * @author: Yukai  
 * @date: 2018年5月17日 上午11:21:19
 */
package com.oscar.gitlabEventCenter.service.handler;

import org.springframework.beans.factory.annotation.Value;

import net.sf.json.JSONObject;

/**
 * @ClassName: BaseEventHandler
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 上午11:21:19
 */
public interface BaseEventHandler {
    public String description();
    public void handle(JSONObject msg);
}

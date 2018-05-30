/**
 * @Title: GitlabMrEventService.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service
 * @author: Yukai  
 * @date: 2018年5月17日 上午10:27:14
 */
package com.oscar.gitlabEventCenter.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oscar.gitlabEventCenter.web.controller.EventDispatcher.EventType;
import com.oscar.gitlabEventCenter.web.service.handler.BaseEventHandler;
import com.oscar.gitlabEventCenter.web.service.handler.EventHandlerRegistry;

import net.sf.json.JSONObject;

/**
 * @ClassName: GitlabMrEventService
 * @Description: Gitlab 提交评审事件
 * @author Yukai
 * @data 2018年5月17日 上午10:27:14
 */
@Service
public class GitlabMrEventService extends BaseEventService{

    @Override
    public void handleEvent(String msg) {
        JSONObject json = JSONObject.fromObject(msg);
        List<BaseEventHandler> eventHandlers = EventHandlerRegistry.instance.getEventHandlers(EventType.MERGE_REQUEST);
        for (BaseEventHandler handler : eventHandlers) {
            submitHandler(handler, json);
        } 
    }

}

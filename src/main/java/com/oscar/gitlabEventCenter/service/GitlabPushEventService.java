/**
 * @Title: GitlabPushEventService.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service
 * @author: Yukai  
 * @date: 2018年5月17日 上午10:03:11
 */
package com.oscar.gitlabEventCenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oscar.gitlabEventCenter.controller.EventDispatcher.EventType;
import com.oscar.gitlabEventCenter.service.handler.BaseEventHandler;
import com.oscar.gitlabEventCenter.service.handler.EventHandlerRegistry;

import net.sf.json.JSONObject;

/**
 * @ClassName: GitlabPushEventService
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 上午10:03:11
 */
public class GitlabPushEventService extends BaseEventService {
    private static Logger logger = LoggerFactory.getLogger(GitlabPushEventService.class);

    @Override
    public void handleEvent(String msg) {
        JSONObject json = JSONObject.fromObject(msg);
        List<BaseEventHandler> eventHandlers = EventHandlerRegistry.instance.getEventHandlers(EventType.PUSH);
        List<Future<?>> futures = new ArrayList<>();
        for (BaseEventHandler handler : eventHandlers) {
            futures.add(submitHandler(handler, json));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("Eventhandler execute failed", e);
            }
        }
    }

}

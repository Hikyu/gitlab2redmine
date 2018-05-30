/**
 * @Title: EventHandlerRegistry.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service
 * @author: Yukai  
 * @date: 2018年5月17日 上午11:16:33
 */
package com.oscar.gitlabEventCenter.web.service.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oscar.gitlabEventCenter.web.controller.EventDispatcher.EventType;

/**
 * @ClassName: EventHandlerRegistry
 * @Description: 管理事件处理器，包括handler注册，取消注册等
 * @author Yukai
 * @data 2018年5月17日 上午11:16:33
 */
public class EventHandlerRegistry {
    private static Logger logger = LoggerFactory.getLogger(EventHandlerRegistry.class);
    public static EventHandlerRegistry instance = new EventHandlerRegistry();
    private Map<EventType, List<BaseEventHandler>> handlerList;

    private EventHandlerRegistry() {
        handlerList = new HashMap<>();
    }

    /**
     * 注册事件处理器
     * 
     * @param eventType
     *            事件类型
     * @param handler
     *            对应的处理器
     */
    public synchronized void registerEventHandler(EventType eventType, BaseEventHandler handler) {
        logger.info("Register event handler: {}, eventType: {}", handler.description(), eventType);
        if (handlerList.containsKey(eventType)) {
            List<BaseEventHandler> list = handlerList.get(eventType);
            list.add(handler);
        } else {
            List<BaseEventHandler> handlers = new ArrayList<>();
            handlers.add(handler);
            handlerList.put(eventType, handlers);
        }
    }

    /**
     * 取消事件处理器
     */
    public synchronized void removeEventHandler(BaseEventHandler handler) {

    }

    /**
     * 获取指定事件类型的事件处理器
     * 
     * @param eventType
     *            指定的事件类型
     * @return
     */
    public synchronized List<BaseEventHandler> getEventHandlers(EventType eventType) {
        List<BaseEventHandler> list = handlerList.get(eventType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return Collections.unmodifiableList(list);
    }

    
    public synchronized void clear() {
        handlerList.clear();
    }
}

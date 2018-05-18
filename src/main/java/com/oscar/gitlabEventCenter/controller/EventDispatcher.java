/**
 * @Title: EventDispatcher.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.controller
 * @author: Yukai  
 * @date: 2018年5月15日 下午4:53:34
 */
package com.oscar.gitlabEventCenter.controller;

import com.oscar.gitlabEventCenter.common.exception.IllegalEventTypeException;
import com.oscar.gitlabEventCenter.service.BaseEventService;
import com.oscar.gitlabEventCenter.service.GitlabMrEventService;
import com.oscar.gitlabEventCenter.service.GitlabPushEventService;

/**
 * @ClassName: EventDispatcher
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月15日 下午4:53:34
 */
public class EventDispatcher {
    private BaseEventService eventService;
    private EventType eventType;

    public enum EventType {
        MERGE_REQUEST("Merge Request Hook"), PUSH("Push Hook"), UNDEFINED("");
        private String name;

        private EventType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static EventType match(String name) {
            if (name == null) {
                return UNDEFINED;
            }
            for (EventType type : EventType.values()) {
                if (name.equals(type.toString())) {
                    return type;
                }
            }
            return EventType.UNDEFINED;
        }
    }

    /**
     * 根据事件类型，选择事件处理器
     * 
     * @param eventType
     */
    public void dispatch(String eventType) {
        this.eventType = EventType.match(eventType);
        switch (this.eventType) {
        case PUSH:
            this.eventService = new GitlabPushEventService();
            break;
        case MERGE_REQUEST:
            this.eventService = new GitlabMrEventService();
            break;
        default:
            throw new IllegalEventTypeException("Undefined Event Type: " + eventType);
        }
    }

    /**
     * 处理gitlab post信息
     * 
     * @param msg
     *            Json 串
     */
    public void handle(String msg) {
        eventService.handleEvent(msg);
    }
}

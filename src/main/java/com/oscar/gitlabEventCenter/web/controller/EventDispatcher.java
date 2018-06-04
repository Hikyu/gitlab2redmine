/**
 * @Title: EventDispatcher.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.controller
 * @author: Yukai  
 * @date: 2018年5月15日 下午4:53:34
 */
package com.oscar.gitlabEventCenter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.exception.IllegalEventTypeException;
import com.oscar.gitlabEventCenter.web.service.BaseEventService;
import com.oscar.gitlabEventCenter.web.service.GitlabMrEventService;
import com.oscar.gitlabEventCenter.web.service.GitlabPushEventService;

/**
 * @ClassName: EventDispatcher
 * @Description: gitlab webhook 事件分发器
 * @author Yukai
 * @data 2018年5月15日 下午4:53:34
 */
@Component
@Scope(value="prototype")
public class EventDispatcher {
    private BaseEventService eventService;
    private EventType eventType;
    @Autowired
    GitlabMrEventService gitlabMrEventService;
    @Autowired
    GitlabPushEventService gitlabPushEventService;

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
            this.eventService = this.gitlabPushEventService;
            break;
        case MERGE_REQUEST:
            this.eventService = this.gitlabMrEventService;
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

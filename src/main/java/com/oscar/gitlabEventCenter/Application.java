package com.oscar.gitlabEventCenter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.oscar.gitlabEventCenter.web.controller.EventDispatcher.EventType;
import com.oscar.gitlabEventCenter.web.service.GitlabPushEventService;
import com.oscar.gitlabEventCenter.web.service.handler.EventHandlerRegistry;
import com.oscar.gitlabEventCenter.web.service.handler.GitlabPush2DBHandler;
import com.oscar.gitlabEventCenter.web.service.handler.GitlabPush2RedmineHandler;

@Configuration
@ComponentScan
@SpringBootApplication
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);
    @Autowired
    private GitlabPush2DBHandler gitlabPush2DBHandler;
    @Autowired
    private GitlabPush2RedmineHandler gitlabPush2RedmineHandler;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info(">>>>>>>>> server start >>>>>>>>>");
    }
    
    @PostConstruct
    public void registerHandler(){
//      EventHandlerRegistry.instance.registerEventHandler(EventType.MERGE_REQUEST, new GitlabMr2RedmineHandler());
//      EventHandlerRegistry.instance.registerEventHandler(EventType.PUSH, new GitlabPush2RedmineHandler());
        EventHandlerRegistry.instance.registerEventHandler(EventType.PUSH, gitlabPush2DBHandler);
    }
    
}

package com.oscar.gitlabEventCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.oscar.gitlabEventCenter.controller.EventDispatcher.EventType;
import com.oscar.gitlabEventCenter.service.handler.EventHandlerRegistry;
import com.oscar.gitlabEventCenter.service.handler.GitlabMr2RedmineHandler;
import com.oscar.gitlabEventCenter.service.handler.GitlabPush2RedmineHandler;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // 注册事件处理器
        EventHandlerRegistry.instance.registerEventHandler(EventType.MERGE_REQUEST, new GitlabMr2RedmineHandler());
        EventHandlerRegistry.instance.registerEventHandler(EventType.PUSH, new GitlabPush2RedmineHandler());
    }
}

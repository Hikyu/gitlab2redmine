/**
 * @Title: BaseEventService.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service
 * @author: Yukai  
 * @date: 2018年5月15日 下午5:00:07
 */
package com.oscar.gitlabEventCenter.web.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.web.service.handler.BaseEventHandler;

import net.sf.json.JSONObject;

/**
 * @ClassName: BaseEventService
 * @Description: Gitlab 推送提交事件
 * @author Yukai
 * @data 2018年5月15日 下午5:00:07
 */
@Service
public abstract class BaseEventService {
    private static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(Config.EXECUTORS_NUM);
    }

    public abstract void handleEvent(String msg);

    /**
     * 同步执行事件处理器
     * 
     * @param handler
     */
    public void executeHandler(BaseEventHandler handler, JSONObject msg) {
        handler.handle(msg);
    }

    /**
     * 异步执行事件处理器
     * 
     * @param handler
     */
    public Future<?> submitHandler(BaseEventHandler handler, JSONObject msg) {
        return executorService.submit(new Runnable() {

            @Override
            public void run() {
                handler.handle(msg);
            }
        });
    }
}

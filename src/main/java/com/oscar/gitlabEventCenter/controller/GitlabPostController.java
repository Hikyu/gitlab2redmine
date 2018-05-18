/**
 * @Title: GitlabPostController.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.controller
 * @author: Yukai  
 * @date: 2018年5月15日 下午3:43:23
 */
package com.oscar.gitlabEventCenter.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oscar.gitlabEventCenter.common.utils.HttpUtils;

/**
 * @ClassName: GitlabPostController
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月15日 下午3:43:23
 */

@RestController
@RequestMapping("/gitlab")
public class GitlabPostController {
    private static Logger logger = LoggerFactory.getLogger(GitlabPostController.class);
    @Value("${gitlab.http.header.eventType}")
    private String eventTypeHeader;

    @RequestMapping(value = "/post",method = RequestMethod.POST)
    public void post(HttpServletRequest request, @RequestBody String post) {
        logger.info("Request from {}, event type: {}", HttpUtils.getReqeustHost(request), HttpUtils.getFromHttpHeader(request, eventTypeHeader));
        logger.debug("post msg: {}", post);
        String eventType = HttpUtils.getFromHttpHeader(request, eventTypeHeader);
        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.dispatch(eventType);
        dispatcher.handle(post);
    }

}

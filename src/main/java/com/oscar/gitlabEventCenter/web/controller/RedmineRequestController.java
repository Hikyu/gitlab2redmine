/**
 * Copyright © 2018天津神舟通用数据技术有限公司. All rights reserved.
 * @Title: RedmineRequestController.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.controller
 * @author: Yukai  
 * @date: 2018年5月23日 下午3:05:37
 */
package com.oscar.gitlabEventCenter.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oscar.gitlabEventCenter.common.exception.IllegalRequestException;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.web.service.RedmineCommitsetService;

/**
 * @ClassName: RedmineRequestController
 * @Description: 处理 redmine request 消息
 * @author Yukai
 * @data 2018年5月23日 下午3:05:37
 */
@RestController
@RequestMapping("/redmine")
public class RedmineRequestController {
    private static Logger logger = LoggerFactory.getLogger(RedmineRequestController.class);

    @Autowired
    private RedmineCommitsetService commitsetService;

    @RequestMapping(value = "/{issueid}/commitset", method = RequestMethod.GET)
    public Page<CommitSet> post(HttpServletRequest request, @PathVariable("issueid") String issueid,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "15") int size) {
        logger.info("Request from {}, Redmine commitset request issueid={}", HttpUtils.getReqeustHost(request),
                issueid);
        long issueID = -1;
        try {
            issueID = Long.valueOf(issueid);
        } catch (NumberFormatException e) {
            throw new IllegalRequestException(String.format("Illegal issueid: %s", issueid), e);
        }
        return commitsetService.getCommitsetByIssueID(issueID, page, size);
    }
}

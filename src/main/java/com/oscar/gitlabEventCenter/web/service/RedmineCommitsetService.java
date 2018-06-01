/**
 * Copyright © 2018天津神舟通用数据技术有限公司. All rights reserved.
 * @Title: RedmineCommitsetService.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.web.service
 * @author: Yukai  
 * @date: 2018年5月28日 上午10:56:16
 */
package com.oscar.gitlabEventCenter.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.repository.CommitSetRepository;

/**
 * @ClassName: RedmineCommitsetService
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月28日 上午10:56:16
 */
@Service
public class RedmineCommitsetService {
    private static Logger logger = LoggerFactory.getLogger(RedmineCommitsetService.class);
    @Autowired
    private CommitSetRepository repository;

    public Page<CommitSet> getCommitsetByIssueID(long issueID, int page, int size) {
        Sort sort = new Sort(Direction.ASC, "timestamp");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommitSet> commitsets = repository.findByIssueId(issueID, pageable);
        return commitsets;
    }
}

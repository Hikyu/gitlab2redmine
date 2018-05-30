/**
 * Copyright © 2018天津神舟通用数据技术有限公司. All rights reserved.
 * @Title: CommitSetRepository.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.repository
 * @author: Yukai  
 * @date: 2018年5月24日 下午2:39:31
 */
package com.oscar.gitlabEventCenter.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;

/**
 * @ClassName: CommitSetRepository
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月24日 下午2:39:31
 */
public interface CommitSetRepository extends PagingAndSortingRepository<CommitSet, Long> {
    Page<CommitSet> findByIssueID(long issueID, Pageable pageable);
}
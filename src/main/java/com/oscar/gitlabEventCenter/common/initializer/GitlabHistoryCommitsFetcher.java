/**
 * @Title: ContextRefreshedListener.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter
 * @author: Yukai  
 * @date: 2018年5月31日 上午10:20:37
 */
package com.oscar.gitlabEventCenter.common.initializer;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.GitlabApi;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.repository.CommitSetRepository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: ContextRefreshedListener
 * @Description: fetch gitlab project history commitset
 * @author Yukai
 * @data 2018年5月31日 上午10:20:37
 */
@Component
public class GitlabHistoryCommitsFetcher implements ApplicationListener<ContextRefreshedEvent> {
    private volatile AtomicBoolean isInit = new AtomicBoolean(false);
    private static Logger logger = LoggerFactory.getLogger(GitlabHistoryCommitsFetcher.class);
    @Autowired
    private ApplicationContext context;
    @Autowired
    private CommitSetRepository repository;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isInit.compareAndSet(false, true)) {
            return;
        }
        logger.info("Fetching gitlab project history commisets ......");
        // 取得最新添加的commitset 时间
        Date lastCommitDate = getLastCommitDate();
        // 获取所有 group 下的 project id
        Set<Integer> groupIds = getGroupIds();
        Set<Integer> projectIds = getProjectIds(groupIds);
        // 并发读取 project history commitsets
        ExecutorService executor = Executors.newFixedThreadPool(Config.EXECUTORS_NUM);
        CompletionService<List<CommitSet>> completionService = new ExecutorCompletionService<List<CommitSet>>(executor); 
        
        try {
            Iterator<Integer> iterator = projectIds.iterator();
            while (iterator.hasNext()) {
                Integer id = iterator.next();
                GitlabProjectCommitsFetcher fetcher = context.getBean(GitlabProjectCommitsFetcher.class);
                fetcher.setProjectId(id);
                fetcher.setLastCommitDate(lastCommitDate);
                completionService.submit(fetcher);
            }
            for (int i = 0; i < projectIds.size(); i++) {
                List<CommitSet> commitList = completionService.take().get();
                for (CommitSet commitSet : commitList) {// 循环依次插入，跳过失败的commitset
                    try {
                        logger.debug("Save commitset {}", commitSet);
                        repository.save(commitSet);
                    } catch (Exception e) {
                        logger.debug("Save commiset failed: {}", commitSet, e);
                        // 忽略不满足唯一性约束抛出的异常
                        if (!(e instanceof DataIntegrityViolationException)) {
                            throw e;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Fetch commitset failed", e);
            throw new IllegalStateException(e);
        } finally {
            executor.shutdown();
        }
    }

    private Date getLastCommitDate() {
        Date lastCommitDate = new Date(Long.MIN_VALUE);
        // 取出最新添加的 commitset 的提交时间
        Sort sort = new Sort(Direction.DESC, "timestamp");
        Pageable pageable = PageRequest.of(0, 1, sort);
        List<CommitSet> commitSets = repository.findAll(pageable).getContent();
        if (!commitSets.isEmpty()) {
            lastCommitDate = commitSets.get(0).getTimestamp();
        }
        return lastCommitDate;
    }
    
    /**
     * 获取所有 group 下的 project id
     * @param groupIds
     * @return
     */
    private Set<Integer> getProjectIds(Set<Integer> groupIds) {
        Set<Integer> projectIds = new HashSet<>();
        Iterator<Integer> iterator = groupIds.iterator();
        while (iterator.hasNext()) {
            int groupId = iterator.next();
            int page = 0;
            int perPage = 100;
            while (true) {
                String res = GitlabApi.fetchGroupProjectsInfo(groupId, page, perPage);
                JSONArray projects = JSONArray.fromObject(res);
                if (projects.isEmpty()) {
                    break;
                }
                Iterator<JSONObject> projectItr = projects.iterator();
                while (projectItr.hasNext()) {
                    JSONObject project = projectItr.next();
                    projectIds.add(project.getInt("id"));
                }
                page ++;
            }
        }
        
        return projectIds;
    }
    
    private Set<Integer> getGroupIds() {
        Set<Integer> groupIds = new HashSet<>();
        int page = 0;
        int perPage = 100;
        while (true) {
            String res = GitlabApi.fetchGroupsInfo(page, perPage);
            JSONArray groups = JSONArray.fromObject(res);
            if (groups.isEmpty()) {
                break;
            }
            Iterator<JSONObject> iterator = groups.iterator();
            while (iterator.hasNext()) {
                JSONObject group = iterator.next();
                groupIds.add(group.getInt("id"));
            }
            page ++;
        }
        return groupIds;
    }

}

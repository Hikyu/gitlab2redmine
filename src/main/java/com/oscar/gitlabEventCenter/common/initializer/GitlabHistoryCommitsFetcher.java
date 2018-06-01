/**
 * @Title: ContextRefreshedListener.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter
 * @author: Yukai  
 * @date: 2018年5月31日 上午10:20:37
 */
package com.oscar.gitlabEventCenter.common.initializer;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.exception.HttpRequestFailed;
import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.repository.CommitSetRepository;
import com.oscar.gitlabEventCenter.web.controller.EventDispatcher;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
        Date lastCommitDate = new Date(Long.MIN_VALUE);
        // 取出最新添加的 commitset 的提交时间
        Sort sort = new Sort(Direction.DESC, "timestamp");
        Pageable pageable = PageRequest.of(0, 1, sort);
        List<CommitSet> commitSets = repository.findAll(pageable).getContent();
        if (!commitSets.isEmpty()) {
            lastCommitDate = commitSets.get(0).getTimestamp();
        }
        // 获取所有 group 下的 project id
        Set<Integer> groupIds = getGroupIds();
        Set<Integer> projectIds = getProjectIds(groupIds);
        // 并发读取 project history commitsets
        ExecutorService executor = Executors.newFixedThreadPool(Config.EXECUTORS_NUM);
        CompletionService<List<CommitSet>> completionService = new ExecutorCompletionService<List<CommitSet>>(executor); 
        
        Iterator<Integer> iterator = projectIds.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            GitlabProjectCommitsFetcher fetcher = context.getBean(GitlabProjectCommitsFetcher.class);
            fetcher.setProjectId(id);
            fetcher.setLastCommitDate(lastCommitDate);
            completionService.submit(fetcher);
        }
        for (int i = 0; i < projectIds.size(); i++) {
            try {
                List<CommitSet> commitList = completionService.take().get();
                for (CommitSet commitSet : commitList) {// 循环依次插入，跳过失败的commitset
                    try {
                        logger.debug("Save commitset {}", commitSet);
                        repository.save(commitSet);
                    } catch (Exception e) {
                        logger.error("Save commiset failed: {}", commitSet, e);
                    }
                }
            } catch (Exception e) {
                logger.error("Project fetch commitset failed", e);
            }
        }
        executor.shutdown();
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
            String projectUrl = String.format(Config.GITLAB_GROUP_PROJECTS_URL, groupId);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(projectUrl).newBuilder();
            urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
            urlBuilder.addQueryParameter("simple", "true");
            Request request = new Request.Builder()
                                         .url(urlBuilder.build())
                                         .build();
            Response response = HttpUtils.sendRequestSync(request);
            try {
                String res = response.body().string();
                if (!response.isSuccessful()) {
                    throw new HttpRequestFailed(String.format("Http request to %s failed, code: %s, response: body %s",
                            request.url().toString(), response.code(), res));
                }
                JSONArray projects = JSONArray.fromObject(res);
                Iterator<JSONObject> projectItr = projects.iterator();
                while (projectItr.hasNext()) {
                    JSONObject project = projectItr.next();
                    projectIds.add(project.getInt("id"));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Fetch gitlab projects failed.", e);
            }
        }
        
        return projectIds;
    }
    
    /**
     * 获取所有的  groupsID
     * @return
     */
    private Set<Integer> getGroupIds() {
        Set<Integer> groupIds = new HashSet<>();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Config.GITLAB_GROUPS_URL).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
        urlBuilder.addQueryParameter("simple", "true");
        Request request = new Request.Builder()
                                     .url(urlBuilder.build())
                                     .build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            String res = response.body().string();
            if (!response.isSuccessful()) {
                throw new HttpRequestFailed(String.format("Http request to %s failed, code: %s, response: body %s",
                        request.url().toString(), response.code(), res));
            }
            JSONArray groups = JSONArray.fromObject(res);
            Iterator<JSONObject> iterator = groups.iterator();
            while (iterator.hasNext()) {
                JSONObject group = iterator.next();
                groupIds.add(group.getInt("id"));
            }
            
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab groups failed.", e);
        }
        return groupIds;
    }

}

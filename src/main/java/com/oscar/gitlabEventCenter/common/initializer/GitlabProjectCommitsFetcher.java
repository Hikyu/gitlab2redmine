/**
 * @Title: GitlabProjectCommitsFetcher.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.initializer
 * @author: Yukai  
 * @date: 2018年5月31日 下午1:51:51
 */
package com.oscar.gitlabEventCenter.common.initializer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.utils.GitlabApi;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet.CommitSetBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: GitlabProjectCommitsFetcher
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月31日 下午1:51:51
 */
@Component
@Scope(value = "prototype")
public class GitlabProjectCommitsFetcher implements Callable<List<CommitSet>> {
    private int projectId;
    private Date lastCommitDate;
    private String projectName;
    private String projectUrl;
    
    @Override
    public List<CommitSet> call() throws Exception {
        getProjectInfo();
        List<CommitSet> commitSets = new ArrayList<>();
        Date since = lastCommitDate;
        Date until = new Date();
        int page = 0;
        int perPage = 100;
        while (true) {
            List<CommitSet> commitsPerPage = fetchCommitPerPage(since, until, page, perPage);
            if (commitsPerPage.isEmpty()) {
                break;
            }
            commitSets.addAll(commitsPerPage);
            page ++;
            // 随机休息若干毫秒
            Thread.sleep(new Random().nextInt(100));
        }
        // retry one time
        since = until;
        until = new Date();
        List<CommitSet> commitsPerPage = fetchCommitPerPage(since, until, 0, perPage);
        commitSets.addAll(commitsPerPage);
        
        return commitSets;
    }
    
    private List<CommitSet> fetchCommitPerPage(Date sinceDate, Date untilDate, int page, int perPage) {
        List<CommitSet> commitSets = new ArrayList<>();
        String res = GitlabApi.fetchProjectCommitsPerPage(projectId, sinceDate, untilDate, page, perPage);
        JSONArray projects = JSONArray.fromObject(res);
        Iterator<JSONObject> commitItr = projects.iterator();
        while (commitItr.hasNext()) {
            JSONObject commit = commitItr.next();
            String commitID = commit.getString("id");
            String message = commit.getString("message");
            String timestampStr = commit.getString("committed_date");
            String author = commit.getString("author_name");
            String commitUrl = String.format(projectUrl + "/commit/%s", commitID);
                    
            String pattern = "#\\d+";
            Matcher matcher = Pattern.compile(pattern).matcher(message);
            Set<String> issueIDList = new HashSet();
            while (matcher.find()) {
                // 去掉前面的#号
                issueIDList.add(matcher.group().substring(1));
            }
            
            List<CommitSet> commitsetsFromCommit = new ArrayList<>();
            if (!issueIDList.isEmpty()) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);
                Date timestamp = Date.from(zonedDateTime.toInstant());
                for (String issueID : issueIDList) {
                    CommitSetBuilder builder = new CommitSetBuilder(Long.valueOf(issueID), commitID);
                    CommitSet commitset = builder.author(author)
                           .commitUrl(commitUrl)
                           .message(message)
                           .projectName(projectName)
                           .projectUrl(projectUrl)
                           .timestamp(timestamp)
                           .build();
                    commitsetsFromCommit.add(commitset);
                }
            }
            commitSets.addAll(commitsetsFromCommit);
        }
        return commitSets;
    }
    
    private void getProjectInfo() {
        String res = GitlabApi.fetchProjectInfo(projectId);
        JSONObject project = JSONObject.fromObject(res);
        projectName = project.getString("name");
        projectUrl = project.getString("web_url");
    }
    
    
    public void setProjectId(int id) {
        this.projectId = id;
    }
    
    public void setLastCommitDate(Date date) {
        this.lastCommitDate = date;
    }

}

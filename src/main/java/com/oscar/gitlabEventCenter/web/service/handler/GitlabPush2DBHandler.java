/**
 * @Title: GitlabPushEventHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service.handler
 * @author: Yukai  
 * @date: 2018年5月23日 下午3:36:20
 */
package com.oscar.gitlabEventCenter.web.service.handler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet.CommitSetBuilder;
import com.oscar.gitlabEventCenter.jpa.repository.CommitSetRepository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: GitlabPushEventHandler
 * @Description: 处理 gitlab webhook push 事件，格式化数据后存入数据库
 * @author Yukai
 * @data 2018年5月23日 下午3:36:20
 */
@Component
public class GitlabPush2DBHandler implements BaseEventHandler {
    private String handlerID = "GitlabMr2RedmineHandler";
    private static Logger logger = LoggerFactory.getLogger(GitlabPush2DBHandler.class);
    
    @Autowired
    private CommitSetRepository repository;

    @Override
    public String description() {
        return String.format("handler: %s", handlerID);
    }
    /**
     * gitlab push 事件 post json 数据：
     * {
          "object_kind": "push",
          "before": "95790bf891e76fee5e1747ab589903a6a1f80f22",
          "after": "da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
          "ref": "refs/heads/master",
          "checkout_sha": "da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
          "user_id": 4,
          "user_name": "John Smith",
          "user_username": "jsmith",
          "user_email": "john@example.com",
          "user_avatar": "https://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=8://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=80",
          "project_id": 15,
          "project":{
            "id": 15,
            "name":"Diaspora",
            "description":"",
            "web_url":"http://example.com/mike/diaspora",
            "avatar_url":null,
            "git_ssh_url":"git@example.com:mike/diaspora.git",
            "git_http_url":"http://example.com/mike/diaspora.git",
            "namespace":"Mike",
            "visibility_level":0,
            "path_with_namespace":"mike/diaspora",
            "default_branch":"master",
            "homepage":"http://example.com/mike/diaspora",
            "url":"git@example.com:mike/diaspora.git",
            "ssh_url":"git@example.com:mike/diaspora.git",
            "http_url":"http://example.com/mike/diaspora.git"
          },
          "repository":{
            "name": "Diaspora",
            "url": "git@example.com:mike/diaspora.git",
            "description": "",
            "homepage": "http://example.com/mike/diaspora",
            "git_http_url":"http://example.com/mike/diaspora.git",
            "git_ssh_url":"git@example.com:mike/diaspora.git",
            "visibility_level":0
          },
          "commits": [
            {
              "id": "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327",
              "message": "Update Catalan translation to e38cb41.",
              "timestamp": "2011-12-12T14:27:31+02:00",
              "url": "http://example.com/mike/diaspora/commit/b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327",
              "author": {
                "name": "Jordi Mallach",
                "email": "jordi@softcatala.org"
              },
              "added": ["CHANGELOG"],
              "modified": ["app/controller/application.rb"],
              "removed": []
            },
            {
              "id": "da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
              "message": "fixed readme",
              "timestamp": "2012-01-03T23:36:29+02:00",
              "url": "http://example.com/mike/diaspora/commit/da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
              "author": {
                "name": "GitLab dev user",
                "email": "gitlabdev@dv6700.(none)"
              },
              "added": ["CHANGELOG"],
              "modified": ["app/controller/application.rb"],
              "removed": []
            }
          ],
          "total_commits_count": 4
        }
     */
    @Override
    public void handle(JSONObject msg) {
        int projectId = msg.getJSONObject("project").getInt("id");
        if (Config.isForkProject(projectId)) {
            return;
        }
        String commitID = msg.getString("checkout_sha");
        JSONArray commits = msg.getJSONArray("commits");
        String message = "";
        String commitUrl = "";
        String timestampStr = "";
        String author = "";
        Iterator<JSONObject> iterator = commits.iterator();
        while (iterator.hasNext()) {// 从多个提交中取出合并提交
            JSONObject commit = iterator.next();
            String sha = commit.getString("id");
            if (sha != null && sha.equals(commitID)) {
                message = commit.getString("message");
                commitUrl = commit.getString("url");
                timestampStr = commit.getString("timestamp");
                author = commit.getJSONObject("author").getString("name");
                break;
            }
        }
        String pattern = "#\\d+";
        Matcher matcher = Pattern.compile(pattern).matcher(message);
        List<String> issueIDList = new ArrayList<>();
        while (matcher.find()) {
            // 去掉前面的#号
            issueIDList.add(matcher.group().substring(1));
        }
        
        if (!issueIDList.isEmpty()) {
            JSONObject project = msg.getJSONObject("project");
            String projectName = project.getString("name");
            String projectUrl = project.getString("web_url");
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);
            Date timestamp = Date.from(zonedDateTime.toInstant());
            List<CommitSet> commitSets = new ArrayList<>();
            StringBuilder log = new StringBuilder("Successful insert commitSets: \n");
            for (String issueID : issueIDList) {
                CommitSetBuilder builder = new CommitSetBuilder(Long.valueOf(issueID), commitID);
                CommitSet commitset = builder.author(author)
                       .commitUrl(commitUrl)
                       .message(message)
                       .projectName(projectName)
                       .projectUrl(projectUrl)
                       .timestamp(timestamp)
                       .build();
                commitSets.add(commitset);
                log.append(commitset);
            }
            repository.saveAll(commitSets);
            logger.debug(log.toString());
        }
    }
    
}

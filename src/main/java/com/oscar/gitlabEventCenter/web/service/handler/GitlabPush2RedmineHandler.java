/**
 * @Title: GitlabPush2RedmineHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service.handler
 * @author: Yukai  
 * @date: 2018年5月17日 下午3:23:13
 */
package com.oscar.gitlabEventCenter.web.service.handler;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.exception.HttpRequestFailed;
import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ClassName: GitlabPush2RedmineHandler
 * @Description: 处理 gitlab push 事件，
 *               匹配 push 信息中 #issueid，将格式化后的消息推送到相应的 redmine issue notes
 * @author Yukai
 * @data 2018年5月17日 下午3:23:13
 */
@Component
public class GitlabPush2RedmineHandler extends Gitlab2RedmineHandler {
    private String handlerID = "GitlabMr2RedmineHandler";
    private static Logger logger = LoggerFactory.getLogger(GitlabPush2RedmineHandler.class);

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
        String ref = msg.getString("ref");
//        if (!ref.endsWith("master")) {
//            logger.info("Just Ignore branch {}", ref);
//            return;
//        }
        String checkoutSha = msg.getString("checkout_sha");
        JSONArray commits = msg.getJSONArray("commits");
        String description = "";
        String commitUri = "";
        Iterator iterator = commits.iterator();
        while (iterator.hasNext()) {
            JSONObject commit = (JSONObject) iterator.next();
            String sha = commit.getString("id");
            if (sha != null && sha.equals(checkoutSha)) {
                description = commit.getString("message");
                commitUri = commit.getString("url");
                break;
            }
        }
        String pattern = "#\\d+";
        Matcher matcher = Pattern.compile(pattern).matcher(description);
        String issueID = "";
        if (matcher.find()) {
            // 去掉前面的#号
            issueID = matcher.group(0).substring(1);
        }
        
        if (!"".equals(issueID)) {
            String name = msg.getString("user_name");
            String userName = msg.getString("user_username");
            JSONObject project = msg.getJSONObject("project");
            String projectName = project.getString("name");
            String projectUri = project.getString("web_url");

            String formatCommit = String.format("\"%s\":%s", checkoutSha, commitUri);
            String formatProject = String.format("\"%s\":%s", projectName, projectUri);
            StringBuilder sBuilder = new StringBuilder(String.format("由 %s 合并 %s 到 Gitlab 项目  %s：", name, formatCommit, formatProject));
            sBuilder.append("\n").append("\n");
            sBuilder.append(description);
            JSONObject notes = new JSONObject();
            notes.put("notes", sBuilder.toString());
            JSONObject issue = new JSONObject();
            issue.put("issue", notes);
            
            Request request = buildRequest(issueID, userName, issue.toString());
            Response response = HttpUtils.sendRequestSync(request);
            if (!response.isSuccessful()) {
                throw new HttpRequestFailed(String.format("Http request to %s failed, code: %s, response: body %s",
                        request.url().toString(), response.code(), response.body()));
            }
            logger.info("Update issue {} successful", issueID);
        }
       
    }
    
}

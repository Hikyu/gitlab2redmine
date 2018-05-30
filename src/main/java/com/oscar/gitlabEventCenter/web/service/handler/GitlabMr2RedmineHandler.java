/**
 * @Title: GitlabMr2RedmineHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service.handler
 * @author: Yukai  
 * @date: 2018年5月17日 下午3:13:10
 */
package com.oscar.gitlabEventCenter.web.service.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.exception.HttpRequestFailed;
import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;

import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ClassName: GitlabMr2RedmineHandler
 * @Description: 处理 gitlab merge request 事件，解析merge request
 *               匹配 merge request 信息中 #issueid，将格式化后的消息推送到相应的 redmine issue notes
 * @author Yukai
 * @data 2018年5月17日 下午3:13:10
 */
@Component
public class GitlabMr2RedmineHandler extends Gitlab2RedmineHandler {
    private String handlerID = "GitlabMr2RedmineHandler";
    private static Logger logger = LoggerFactory.getLogger(GitlabMr2RedmineHandler.class);

    /**
     * gitlab merge request 事件 json 格式数据：
     * {
            "object_kind": "merge_request",
            "user": {
              "name": "Administrator",
              "username": "root",
              "avatar_url": "http://www.gravatar.com/avatar/e64c7d89f26bd1972efa854d13d7dd61?s=40\u0026d=identicon"
            },
            "project": {
              "id": 1,
              "name":"Gitlab Test",
              "description":"Aut reprehenderit ut est.",
              "web_url":"http://example.com/gitlabhq/gitlab-test",
              "avatar_url":null,
              "git_ssh_url":"git@example.com:gitlabhq/gitlab-test.git",
              "git_http_url":"http://example.com/gitlabhq/gitlab-test.git",
              "namespace":"GitlabHQ",
              "visibility_level":20,
              "path_with_namespace":"gitlabhq/gitlab-test",
              "default_branch":"master",
              "homepage":"http://example.com/gitlabhq/gitlab-test",
              "url":"http://example.com/gitlabhq/gitlab-test.git",
              "ssh_url":"git@example.com:gitlabhq/gitlab-test.git",
              "http_url":"http://example.com/gitlabhq/gitlab-test.git"
            },
            "repository": {
              "name": "Gitlab Test",
              "url": "http://example.com/gitlabhq/gitlab-test.git",
              "description": "Aut reprehenderit ut est.",
              "homepage": "http://example.com/gitlabhq/gitlab-test"
            },
            "object_attributes": {
              "id": 99,
              "target_branch": "master",
              "source_branch": "ms-viewport",
              "source_project_id": 14,
              "author_id": 51,
              "assignee_id": 6,
              "title": "MS-Viewport",
              "created_at": "2013-12-03T17:23:34Z",
              "updated_at": "2013-12-03T17:23:34Z",
              "milestone_id": null,
              "state": "opened",
              "merge_status": "unchecked",
              "target_project_id": 14,
              "iid": 1,
              "description": "",
              "source": {
                "name":"Awesome Project",
                "description":"Aut reprehenderit ut est.",
                "web_url":"http://example.com/awesome_space/awesome_project",
                "avatar_url":null,
                "git_ssh_url":"git@example.com:awesome_space/awesome_project.git",
                "git_http_url":"http://example.com/awesome_space/awesome_project.git",
                "namespace":"Awesome Space",
                "visibility_level":20,
                "path_with_namespace":"awesome_space/awesome_project",
                "default_branch":"master",
                "homepage":"http://example.com/awesome_space/awesome_project",
                "url":"http://example.com/awesome_space/awesome_project.git",
                "ssh_url":"git@example.com:awesome_space/awesome_project.git",
                "http_url":"http://example.com/awesome_space/awesome_project.git"
              },
              "target": {
                "name":"Awesome Project",
                "description":"Aut reprehenderit ut est.",
                "web_url":"http://example.com/awesome_space/awesome_project",
                "avatar_url":null,
                "git_ssh_url":"git@example.com:awesome_space/awesome_project.git",
                "git_http_url":"http://example.com/awesome_space/awesome_project.git",
                "namespace":"Awesome Space",
                "visibility_level":20,
                "path_with_namespace":"awesome_space/awesome_project",
                "default_branch":"master",
                "homepage":"http://example.com/awesome_space/awesome_project",
                "url":"http://example.com/awesome_space/awesome_project.git",
                "ssh_url":"git@example.com:awesome_space/awesome_project.git",
                "http_url":"http://example.com/awesome_space/awesome_project.git"
              },
              "last_commit": {
                "id": "da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
                "message": "fixed readme",
                "timestamp": "2012-01-03T23:36:29+02:00",
                "url": "http://example.com/awesome_space/awesome_project/commits/da1560886d4f094c3e6c9ef40349f7d38b5d27d7",
                "author": {
                  "name": "GitLab dev user",
                  "email": "gitlabdev@dv6700.(none)"
                }
              },
              "work_in_progress": false,
              "url": "http://example.com/diaspora/merge_requests/1",
              "action": "open",
              "assignee": {
                "name": "User1",
                "username": "user1",
                "avatar_url": "http://www.gravatar.com/avatar/e64c7d89f26bd1972efa854d13d7dd61?s=40\u0026d=identicon"
              }
            },
            "labels": [{
              "id": 206,
              "title": "API",
              "color": "#ffffff",
              "project_id": 14,
              "created_at": "2013-12-03T17:15:43Z",
              "updated_at": "2013-12-03T17:15:43Z",
              "template": false,
              "description": "API related issues",
              "type": "ProjectLabel",
              "group_id": 41
            }],
            "changes": {
              "updated_by_id": [null, 1],
              "updated_at": ["2017-09-15 16:50:55 UTC", "2017-09-15 16:52:00 UTC"],
              "labels": {
                "previous": [{
                  "id": 206,
                  "title": "API",
                  "color": "#ffffff",
                  "project_id": 14,
                  "created_at": "2013-12-03T17:15:43Z",
                  "updated_at": "2013-12-03T17:15:43Z",
                  "template": false,
                  "description": "API related issues",
                  "type": "ProjectLabel",
                  "group_id": 41
                }],
                "current": [{
                  "id": 205,
                  "title": "Platform",
                  "color": "#123123",
                  "project_id": 14,
                  "created_at": "2013-12-03T17:15:43Z",
                  "updated_at": "2013-12-03T17:15:43Z",
                  "template": false,
                  "description": "Platform related issues",
                  "type": "ProjectLabel",
                  "group_id": 41
                }]
              }
            }
        }
     */
    @Override
    public void handle(JSONObject msg) {
          // 与 push 事件有重复，暂时注掉
//        JSONObject user = msg.getJSONObject("user");
//        String name = user.getString("name");
//        String userName = user.getString("username");
//        JSONObject project = msg.getJSONObject("project");
//        String projectName = project.getString("name");
//        String projectUri = project.getString("web_url");
//        JSONObject objAttr = msg.getJSONObject("object_attributes");
//        String mrUri = objAttr.getString("url");
//        String description = objAttr.getString("description");
//        String targetBranch = objAttr.getJSONObject("targe").getString("name");
//        
//        String pattern = "#\\d+";
//        Matcher matcher = Pattern.compile(pattern).matcher(description);
//        String issueID = "";
//        if (matcher.find()) {
//            // 去掉前面的#号
//            issueID = matcher.group(0).substring(1);
//        }
//        
//        if (!"".equals(issueID)) {
//            StringBuilder sBuilder = new StringBuilder(String.format("由 %s 合并代码到项目  %s %s 分支：", name, projectName, targetBranch));
//            sBuilder.append("\n");
//            sBuilder.append(String.format("项目地址：%s", projectUri));
//            sBuilder.append(String.format("Merge Request地址：%s", mrUri));
//            JSONObject notes = new JSONObject();
//            notes.put("notes", "lalala");
//            JSONObject issue = new JSONObject();
//            issue.put("issue", notes);
//            
//            Request request = buildRequest(issueID, userName, issue.toString());
//            Response response = HttpUtils.sendRequestSync(request);
//            if (!response.isSuccessful()) {
//                throw new HttpRequestFailed(String.format("Http request to %s failed", request.url().toString()));
//            }
//        }
       
    }

    @Override
    public String description() {
        return String.format("handler: %s", handlerID);
    }

}

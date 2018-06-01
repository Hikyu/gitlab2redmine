/**
 * @Title: GitlabProjectCommitsFetcher.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.initializer
 * @author: Yukai  
 * @date: 2018年5月31日 下午1:51:51
 */
package com.oscar.gitlabEventCenter.common.initializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oscar.gitlabEventCenter.common.exception.HttpRequestFailed;
import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet.CommitSetBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

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
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String since = sf.format(lastCommitDate);
        String until = sf.format(new Date());
        while (true) {
            String commitsUrl = String.format(Config.GITLAB_PROJECTS_COMMITS_URL, projectId);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(commitsUrl).newBuilder();
            urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
            urlBuilder.addQueryParameter("since", since);
            urlBuilder.addQueryParameter("until", until);
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
                if (projects.size() == 0) {// 该 project 的所有 commits 已经取出
                    break;
                }
                Iterator<JSONObject> commitItr = projects.iterator();
                while (commitItr.hasNext()) {
                    List<CommitSet> commitsetsFromCommit = getCommitsetsFromCommit(commitItr.next());
                    commitSets.addAll(commitsetsFromCommit);
                }
                since = until;
                until = sf.format(new Date());
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Fetch gitlab project %s commits failed.", projectId), e);
            }
        }
        
        return commitSets;
    }
    
    /**
     * {
         "id": 3,
         "description": null,
         "default_branch": "master",
         "visibility": "private",
         "ssh_url_to_repo": "git@example.com:diaspora/diaspora-project-site.git",
         "http_url_to_repo": "http://example.com/diaspora/diaspora-project-site.git",
         "web_url": "http://example.com/diaspora/diaspora-project-site",
         "readme_url": "http://example.com/diaspora/diaspora-project-site/blob/master/README.md",
         "tag_list": [
           "example",
           "disapora project"
         ],
         "owner": {
           "id": 3,
           "name": "Diaspora",
           "created_at": "2013-09-30T13:46:02Z"
         },
         "name": "Diaspora Project Site",
         "name_with_namespace": "Diaspora / Diaspora Project Site",
         "path": "diaspora-project-site",
         "path_with_namespace": "diaspora/diaspora-project-site",
         "issues_enabled": true,
         "open_issues_count": 1,
         "merge_requests_enabled": true,
         "jobs_enabled": true,
         "wiki_enabled": true,
         "snippets_enabled": false,
         "resolve_outdated_diff_discussions": false,
         "container_registry_enabled": false,
         "created_at": "2013-09-30T13:46:02Z",
         "last_activity_at": "2013-09-30T13:46:02Z",
         "creator_id": 3,
         "namespace": {
           "id": 3,
           "name": "Diaspora",
           "path": "diaspora",
           "kind": "group",
           "full_path": "diaspora"
         },
         "import_status": "none",
         "import_error": null,
         "permissions": {
           "project_access": {
             "access_level": 10,
             "notification_level": 3
           },
           "group_access": {
             "access_level": 50,
             "notification_level": 3
           }
         },
         "archived": false,
         "avatar_url": "http://example.com/uploads/project/avatar/3/uploads/avatar.png",
         "shared_runners_enabled": true,
         "forks_count": 0,
         "star_count": 0,
         "runners_token": "b8bc4a7a29eb76ea83cf79e4908c2b",
         "public_jobs": true,
         "shared_with_groups": [
           {
             "group_id": 4,
             "group_name": "Twitter",
             "group_access_level": 30
           },
           {
             "group_id": 3,
             "group_name": "Gitlab Org",
             "group_access_level": 10
           }
         ],
         "repository_storage": "default",
         "only_allow_merge_if_pipeline_succeeds": false,
         "only_allow_merge_if_all_discussions_are_resolved": false,
         "printing_merge_requests_link_enabled": true,
         "request_access_enabled": false,
         "merge_method": "merge",
         "approvals_before_merge": 0,
         "statistics": {
           "commit_count": 37,
           "storage_size": 1038090,
           "repository_size": 1038090,
           "lfs_objects_size": 0,
           "job_artifacts_size": 0
         },
         "_links": {
           "self": "http://example.com/api/v4/projects",
           "issues": "http://example.com/api/v4/projects/1/issues",
           "merge_requests": "http://example.com/api/v4/projects/1/merge_requests",
           "repo_branches": "http://example.com/api/v4/projects/1/repository_branches",
           "labels": "http://example.com/api/v4/projects/1/labels",
           "events": "http://example.com/api/v4/projects/1/events",
           "members": "http://example.com/api/v4/projects/1/members"
         }
       }
     */
    private void getProjectInfo() {
        String url = String.format(Config.GITLAB_PROJECT_URL, projectId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
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
            JSONObject project = JSONObject.fromObject(res);
            projectName = project.getString("name");
            projectUrl = project.getString("web_url");
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Fetch gitlab project %s info failed.", projectId), e);
        }
    }
    
    /**
     * 
     * @param commit
              {
                "id": "ed899a2f4b50b4370feeea94676502b42383c746",
                "short_id": "ed899a2f4b5",
                "title": "Replace sanitize with escape once",
                "author_name": "Dmitriy Zaporozhets",
                "author_email": "dzaporozhets@sphereconsultinginc.com",
                "authored_date": "2012-09-20T11:50:22+03:00",
                "committer_name": "Administrator",
                "committer_email": "admin@example.com",
                "committed_date": "2012-09-20T11:50:22+03:00",
                "created_at": "2012-09-20T11:50:22+03:00",
                "message": "Replace sanitize with escape once",
                "parent_ids": [
                  "6104942438c14ec7bd21c6cd5bd995272b3faff6"
                ]
              }
     * @return
     */
    private List<CommitSet> getCommitsetsFromCommit(JSONObject commit) {
        String commitID = commit.getString("id");
        String message = commit.getString("message");
        String timestampStr = commit.getString("committed_date");
        String author = commit.getString("author_name");
        String commitUrl = String.format(projectUrl + "/commit/%s", commitID);
                
        String pattern = "#\\d+";
        Matcher matcher = Pattern.compile(pattern).matcher(message);
        List<String> issueIDList = new ArrayList<>();
        while (matcher.find()) {
            // 去掉前面的#号
            issueIDList.add(matcher.group().substring(1));
        }
        
        List<CommitSet> commitSets = new ArrayList<>();
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
                commitSets.add(commitset);
            }
        }
        return commitSets;
    }

    public void setProjectId(int id) {
        this.projectId = id;
    }
    
    public void setLastCommitDate(Date date) {
        this.lastCommitDate = date;
    }

}

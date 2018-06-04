/**
 * @Title: GitlabApi.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.utils
 * @author: Yukai  
 * @date: 2018年6月1日 下午2:01:58
 */
package com.oscar.gitlabEventCenter.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName: GitlabApi
 * @Description: TODO
 * @author Yukai
 * @data 2018年6月1日 下午2:01:58
 */
public class GitlabApi {
    
    /**
     *{
     *  "id": "ed899a2f4b50b4370feeea94676502b42383c746",
     *  "short_id": "ed899a2f4b5",
     *  "title": "Replace sanitize with escape once",
     *  "author_name": "Dmitriy Zaporozhets",
     *  "author_email": "dzaporozhets@sphereconsultinginc.com",
     *  "authored_date": "2012-09-20T11:50:22+03:00",
     *  "committer_name": "Administrator",
     *  "committer_email": "admin@example.com",
     *  "committed_date": "2012-09-20T11:50:22+03:00",
     *  "created_at": "2012-09-20T11:50:22+03:00",
     *  "message": "Replace sanitize with escape once",
     *  "parent_ids": [
     *    "6104942438c14ec7bd21c6cd5bd995272b3faff6"
     *  ]
     *}
     * @param projectId
     * @param sinceDate
     * @param untilDate
     * @param page
     * @param perPage
     * @return
     */
    public static String fetchProjectCommitsPerPage(int projectId, Date sinceDate, Date untilDate, int page,
            int perPage) {
        if (sinceDate == null) {
            sinceDate = new Date(Integer.MIN_VALUE);
        }
        if (untilDate == null) {
            untilDate = new Date();
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String since = sf.format(sinceDate);
        String until = sf.format(untilDate);
        String commitsUrl = String.format(Config.GITLAB_PROJECTS_COMMITS_URL, projectId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(commitsUrl).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
        urlBuilder.addQueryParameter("since", since);
        urlBuilder.addQueryParameter("until", until);
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("per_page", String.valueOf(perPage));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            return HttpUtils.getResponseBody(response);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Fetch gitlab project %s commits failed.", projectId), e);
        }
    }
    
    /**
     * 
     *{
     *  "id": 3,
     *  "description": null,
     *  "default_branch": "master",
     *  "visibility": "private",
     *  "ssh_url_to_repo": "git@example.com:diaspora/diaspora-project-site.git",
     *  "http_url_to_repo": "http://example.com/diaspora/diaspora-project-site.git",
     *  "web_url": "http://example.com/diaspora/diaspora-project-site",
     *  "readme_url": "http://example.com/diaspora/diaspora-project-site/blob/master/README.md",
     *  "tag_list": [
     *    "example",
     *    "disapora project"
     *  ],
     *  "owner": {
     *    "id": 3,
     *    "name": "Diaspora",
     *    "created_at": "2013-09-30T13:46:02Z"
     *  },
     *  "name": "Diaspora Project Site",
     *  "name_with_namespace": "Diaspora / Diaspora Project Site",
     *  "path": "diaspora-project-site",
     *  "path_with_namespace": "diaspora/diaspora-project-site",
     *  "issues_enabled": true,
     *  "open_issues_count": 1,
     *  "merge_requests_enabled": true,
     *  "jobs_enabled": true,
     *  "wiki_enabled": true,
     *  "snippets_enabled": false,
     *  "resolve_outdated_diff_discussions": false,
     *  "container_registry_enabled": false,
     *  "created_at": "2013-09-30T13:46:02Z",
     *  "last_activity_at": "2013-09-30T13:46:02Z",
     *  "creator_id": 3,
     *  "namespace": {
     *    "id": 3,
     *    "name": "Diaspora",
     *    "path": "diaspora",
     *    "kind": "group",
     *    "full_path": "diaspora"
     *  },
     *  "import_status": "none",
     *  "import_error": null,
     *  "permissions": {
     *    "project_access": {
     *      "access_level": 10,
     *      "notification_level": 3
     *    },
     *    "group_access": {
     *      "access_level": 50,
     *      "notification_level": 3
     *    }
     *  },
     *  "archived": false,
     *  "avatar_url": "http://example.com/uploads/project/avatar/3/uploads/avatar.png",
     *  "shared_runners_enabled": true,
     *  "forks_count": 0,
     *  "star_count": 0,
     *  "runners_token": "b8bc4a7a29eb76ea83cf79e4908c2b",
     *  "public_jobs": true,
     *  "shared_with_groups": [
     *    {
     *      "group_id": 4,
     *      "group_name": "Twitter",
     *      "group_access_level": 30
     *    },
     *    {
     *      "group_id": 3,
     *      "group_name": "Gitlab Org",
     *      "group_access_level": 10
     *    }
     *  ],
     *  "repository_storage": "default",
     *  "only_allow_merge_if_pipeline_succeeds": false,
     *  "only_allow_merge_if_all_discussions_are_resolved": false,
     *  "printing_merge_requests_link_enabled": true,
     *  "request_access_enabled": false,
     *  "merge_method": "merge",
     *  "approvals_before_merge": 0,
     *  "statistics": {
     *    "commit_count": 37,
     *    "storage_size": 1038090,
     *    "repository_size": 1038090,
     *    "lfs_objects_size": 0,
     *    "job_artifacts_size": 0
     *  },
     *  "_links": {
     *    "self": "http://example.com/api/v4/projects",
     *    "issues": "http://example.com/api/v4/projects/1/issues",
     *    "merge_requests": "http://example.com/api/v4/projects/1/merge_requests",
     *    "repo_branches": "http://example.com/api/v4/projects/1/repository_branches",
     *    "labels": "http://example.com/api/v4/projects/1/labels",
     *    "events": "http://example.com/api/v4/projects/1/events",
     *    "members": "http://example.com/api/v4/projects/1/members"
     *  }
     *}
     * @param projectId
     * @return
     */
    public static String fetchProjectInfo(int projectId) {
        String url = String.format(Config.GITLAB_PROJECT_URL, projectId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            return HttpUtils.getResponseBody(response);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Fetch gitlab project %s info failed.", projectId), e);
        }
    }

    public static String fetchGroupsInfo(int page, int perPage) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Config.GITLAB_GROUPS_URL).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
        urlBuilder.addQueryParameter("simple", "true");
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("per_page", String.valueOf(perPage));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            return HttpUtils.getResponseBody(response);
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab groups failed.", e);
        }
    }

    public static String fetchGroupProjectsInfo(int groupId, int page, int perPage) {
        String projectUrl = String.format(Config.GITLAB_GROUP_PROJECTS_URL, groupId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(projectUrl).newBuilder();
        urlBuilder.addQueryParameter("private_token", Config.GITLAB_AUTHKEY);
        urlBuilder.addQueryParameter("simple", "true");
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("per_page", String.valueOf(perPage));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            return HttpUtils.getResponseBody(response);
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab projects failed.", e);
        }
    }
}

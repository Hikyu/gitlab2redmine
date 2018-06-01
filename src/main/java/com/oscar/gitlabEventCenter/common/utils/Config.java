/**
 * @Title: Config.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.utils
 * @author: Yukai  
 * @date: 2018年5月17日 下午2:52:33
 */
package com.oscar.gitlabEventCenter.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Config
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月17日 下午2:52:33
 */
@Component
public class Config {
    public static int EXECUTORS_NUM = 4;
    public static String REDMINE_DOMAIN;
    public static String REDMINE_AUTHKEY;
    public static String REDMINE_AUTHKEY_HEADER;
    public static String REDMINE_SWITCHUSER_HEADER;
    public static String REDMINE_ISSUES_URL;
    public static String REDMINE_MEDIATYPE;
    
    public static String GITLAB_AUTHKEY;
    public static String GITLAB_GROUPS_URL;
    public static String GITLAB_GROUP_PROJECTS_URL;
    public static String GITLAB_PROJECTS_COMMITS_URL;
    public static String GITLAB_PROJECT_URL;

    @Value("${redmine.domain}")
    public void setRedmineDomain(String domain) {
        REDMINE_DOMAIN = domain;
    }

    @Value("${redmine.authkey}")
    public void setRedmineAuthKey(String authKey) {
        REDMINE_AUTHKEY = authKey;
    }

    @Value("${redmine.http.header.apiKey}")
    public void setRedmineAuthKeyHeader(String header) {
        REDMINE_AUTHKEY_HEADER = header;
    }

    @Value("${redmine.http.header.switchUser}")
    public void setRedmineSwitchUserHeader(String header) {
        REDMINE_SWITCHUSER_HEADER = header;
    }
    
    @Value("${redmine.http.mediaType}")
    public void setRedmineHttpMediatype(String mediaType) {
        REDMINE_MEDIATYPE = mediaType;
    }
    
    @Value("${redmine.issue.url}")
    public void setRedmineIssueUrl(String url) {
        REDMINE_ISSUES_URL = url;
    }
    
    @Value("${gitlab.groups.url}")
    public void setGitlabGroupsUrl(String url) {
        GITLAB_GROUPS_URL = url;
    }
    
    @Value("${gitlab.groups.projects.url}")
    public void setGitlabGroupProjectsUrl(String url) {
        GITLAB_GROUP_PROJECTS_URL = url;
    }
    
    @Value("${gitlab.authKey}")
    public void setGitlabAuthkey(String authKey) {
        GITLAB_AUTHKEY = authKey;
    }
    
    @Value("${gitlab.projects.commits.url}")
    public void setGitlabProjectsCommitsUrl(String url) {
        GITLAB_PROJECTS_COMMITS_URL= url;
    } 
    
    @Value("${gitlab.projects.project.url}")
    public void setGitlabProjectUrl(String url) {
        GITLAB_PROJECT_URL= url;
    } 
    
}

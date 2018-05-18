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
}

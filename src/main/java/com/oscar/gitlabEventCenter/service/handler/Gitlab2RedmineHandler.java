/**
 * @Title: Gitlab2RedmineHandler.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.service.handler
 * @author: Yukai  
 * @date: 2018年5月18日 上午11:05:17
 */
package com.oscar.gitlabEventCenter.service.handler;

import com.oscar.gitlabEventCenter.common.utils.Config;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @ClassName: Gitlab2RedmineHandler
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月18日 上午11:05:17
 */
public abstract class Gitlab2RedmineHandler implements BaseEventHandler {
    /**
     * 构建更新issue的http请求
     * @param issueID
     *              更新的问题id
     * @param switchUser
     *              使用哪个用户更新
     * @param updateContent
     *              提交的json对象
     * @return
     */
    protected Request buildRequest(String issueID, String switchUser, String updateContent) {
        String redmineIssueUpdateUri = String.format(Config.REDMINE_ISSUES_URL, issueID);
        MediaType JSON = MediaType.parse(Config.REDMINE_MEDIATYPE);
        RequestBody body = RequestBody.create(JSON, updateContent);
        Request request = new Request.Builder()
                                     .url(redmineIssueUpdateUri)
                                     .addHeader(Config.REDMINE_AUTHKEY_HEADER, Config.REDMINE_AUTHKEY)
                                     .addHeader(Config.REDMINE_SWITCHUSER_HEADER, switchUser)
                                     .put(body)
                                     .build();
        return request;
    }

}

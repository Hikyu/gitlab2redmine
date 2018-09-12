package com.oscar.gitlabEventCenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;

import com.oscar.gitlabEventCenter.common.utils.Config;
import com.oscar.gitlabEventCenter.common.utils.HttpUtils;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebHookGenerator {
    static final String token = "_9LBvxfs1Rw8B1Qx3-xm";

    public static void main(String[] args) {
        Set<Integer> groupIds = fetchGroupsIds();
        for (int groupId : groupIds) {
            Set<Integer> projectIds = fetchGroupProjectsIds(groupId);
            for (int projectId : projectIds) {
                deleteProjectHook(projectId);
                postHookForProject(projectId);
            }
        }

    }
    public static void deleteProjectHook(int projectId) {
        Set<Integer> hookIds = getProjectHooks(projectId);
        for (Integer hookId : hookIds) {
            String groupsUrl = String.format("http://192.168.101.120/api/v4/projects/%s/hooks/%s", projectId, hookId);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(groupsUrl).newBuilder();
            urlBuilder.addQueryParameter("private_token", token);
            urlBuilder.addQueryParameter("id", String.valueOf(projectId));
            urlBuilder.addQueryParameter("hook_id", String.valueOf(hookId));
            Request request = new Request.Builder().url(urlBuilder.build()).delete().build();
            Response response = HttpUtils.sendRequestSync(request);
            try {
                System.out.println(HttpUtils.getResponseBody(response));
            } catch (IOException e) {
                throw new IllegalStateException("Fetch gitlab projects failed.", e);
            }
        }
    }
    public static Set<Integer> getProjectHooks(int projectId) {
        String groupsUrl = String.format("http://192.168.101.120/api/v4/projects/%s/hooks", projectId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(groupsUrl).newBuilder();
        urlBuilder.addQueryParameter("private_token", token);
        urlBuilder.addQueryParameter("page", String.valueOf(0));
        urlBuilder.addQueryParameter("per_page", String.valueOf(100));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            JSONArray hooksArray = JSONArray.fromObject(HttpUtils.getResponseBody(response));

            Iterator<JSONObject> iteratorG = hooksArray.iterator();
            Set<Integer> hookIds = new HashSet<Integer>();
            while (iteratorG.hasNext()) {
                JSONObject hook = iteratorG.next();
                hookIds.add(hook.getInt("id"));
            }
            return hookIds;
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab projects failed.", e);
        }
    }
    
    public static void postHookForProject(int projectId) {
        String url = String.format("http://192.168.101.120/api/v4/projects/%s/hooks", projectId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("private_token", token);// cxhm7Ua_S6JdFdz9ZHnX
        MediaType mediaType = MediaType.parse("application/json");
        // 使用JSONObject封装参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", projectId);
        jsonObject.put("url", "http://192.168.101.207:1219/gitlab/post");
        jsonObject.put("push_events", "true");
        jsonObject.put("enable_ssl_verification", "false");
        // 创建RequestBody对象，将参数按照指定的MediaType封装
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder().url(urlBuilder.build()).post(requestBody).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            System.out.println(HttpUtils.getResponseBody(response));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Fetch gitlab project %s info failed."), e);
        }
    }

    public static Set<Integer> fetchGroupProjectsIds(int groupId) {
        String projectUrl = String.format("http://192.168.101.120/api/v4/groups/%s/projects", groupId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(projectUrl).newBuilder();
        urlBuilder.addQueryParameter("private_token", token);
        urlBuilder.addQueryParameter("simple", "true");
        urlBuilder.addQueryParameter("page", String.valueOf(0));
        urlBuilder.addQueryParameter("per_page", String.valueOf(100));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            JSONArray projectArray = JSONArray.fromObject(HttpUtils.getResponseBody(response));
            Iterator<JSONObject> iteratorI = projectArray.iterator();
            Set<Integer> projectIds = new HashSet<Integer>();
            while (iteratorI.hasNext()) {
                JSONObject project = iteratorI.next();
                projectIds.add(project.getInt("id"));
            }
            return projectIds;
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab projects failed.", e);
        }
    }

    public static Set<Integer> fetchGroupsIds() {
        String groupsUrl = String.format("http://192.168.101.120/api/v4/groups");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(groupsUrl).newBuilder();
        urlBuilder.addQueryParameter("private_token", token);
        urlBuilder.addQueryParameter("page", String.valueOf(0));
        urlBuilder.addQueryParameter("per_page", String.valueOf(100));
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = HttpUtils.sendRequestSync(request);
        try {
            JSONArray groupsArray = JSONArray.fromObject(HttpUtils.getResponseBody(response));
            Iterator<JSONObject> iteratorG = groupsArray.iterator();
            Set<Integer> groupIds = new HashSet<Integer>();
            while (iteratorG.hasNext()) {
                JSONObject group = iteratorG.next();
                groupIds.add(group.getInt("id"));
            }
            return groupIds;
        } catch (IOException e) {
            throw new IllegalStateException("Fetch gitlab projects failed.", e);
        }

    }
}

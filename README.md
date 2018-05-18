# gitlab webhooks && redmine rest

[![GitHub license](https://img.shields.io/github/license/Hikyu/gitlab2redmine.svg)](https://github.com/Hikyu/gitlab2redmine/blob/master/LICENSE)


gitlab webhooks 服务端，监听 gitlab push 事件，更新相关的内容到 redmine 对应 issue 的 notes。

## Install

```
git clone https://github.com/Hikyu/gitlab2redmine.git
mvn package
```

打包生成的相关文件位于 release 目录下。

## Deployment

### gitlab2redmine

1. 编辑 application.yml 配置文件，其中

```
server.port 配置服务端口
redmine.issue.url 配置 redmine issue restAPI 地址
redmine.authKey 配置 redmine 管理员密钥
```

2. 进入 release 目录，执行：

```
nohup java -jar gitlab2redmine.jar &
```

服务已启动。

### gitlab

打开 gitlab 项目，settings->Integrations， URL 文本框输入 gitlab2redmine 服务地址，比如：http://192.168.1.70:1219/gitlab/post

去掉勾选 `Enable SSL verification`

点击 `Add webhook`

### redmine 

登录管理员账户，管理->配置->API，勾选 `启用REST web service`、`启用JSONP支持`

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

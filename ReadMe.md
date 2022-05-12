### 命令行运行

```bash
java -jar java -jar xixunyun-0.0.1-SNAPSHOT.jar --xixunyun.username=习讯云账号 --xixunyun.password=密码 --pushplus.token=推送token
```

### pushplus

pushplus(推送加)是集成了微信、企业微信、钉钉、短信、邮件等渠道的信息推送平台

只需要调用一个简单的API接口，即可帮助你迅速完成消息推送，使用简单方便

我们的所做的一切只是为了让推送变的更简单（复制自<a href= "https://pushplus.plus/">官网</a>）

![image-20220512212156872](https://cdn.jsdelivr.net/gh/2414690715/imgPool/img/2022051261814805c3c2fe472fff1d7d0316ba51-1719b3.png)

微信扫码登录复制你的token

### Docker

```bash
FROM java:8

# 作者

MAINTAINER lyx

# VOLUME 指定临时文件目录为/tmp，在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp

VOLUME /tmp

# 将jar包添加到容器中并更名为lyx_docker.jar

ADD xixunyun-0.0.1-SNAPSHOT.jar lyx_docker.jar

# 运行jar包

RUN bash -c 'touch /lyx_docker.jar'

#推送token 官网：https://pushplus.plus/
ENTRYPOINT ["java","-jar","/lyx_docker.jar","--xixunyun.username=你的用户名","--xixunyun.password=你的密码","--pushplus.token=你的token"]
```


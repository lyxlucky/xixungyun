FROM java:8
# 作者
MAINTAINER lyx
# VOLUME 指定临时文件目录为/tmp，在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为lyx_docker.jar
ADD xixunyun-0.0.1-SNAPSHOT.jar lyx_docker.jar
# 运行jar包
RUN bash -c 'touch /lyx_docker.jar'
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo ‘Asia/Shanghai’ >/etc/timezone
#推送token 官网：https://pushplus.plus/
ENTRYPOINT ["java","-jar","/lyx_docker.jar","--xixunyun.username=习讯云账号","--xixunyun.password=习讯云密码","--pushplus.token=pushPlustoken","--xixunyun.familyName=紧急联系人姓名","--xixunyun.familyPhone=电话"]
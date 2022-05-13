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
ENTRYPOINT ["java","-jar","/lyx_docker.jar","--xixunyun.username=你的用户名","--xixunyun.password=你的密码","--pushplus.token=你的token","--xixunyun.familyName=紧急联系人姓名","--xixunyun.familyPhone=紧急联系人电话"]
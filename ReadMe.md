### 说明

<<<<<<< HEAD
本项目为了解放双手而诞生，代码较为粗糙，大佬勿喷，喜欢请给个star，谢谢！

### 主要功能

#### 日报

默认每天20:00运行，可自定义上传内容，请参考config下的<a href="#">day.properties</a>

#### 周报

默认每过七天运行一次，可自定义上传内容，请参考config下的<a href="#">week.properties</a>

#### 月报

默认每月20号18点运行，可自定义上传内容，请参考config下的<a href="#">month.properties</a>

#### 每日签到

默认每天6点运行，暂时不完美，只支持生机的同学使用（懂得都懂）

#### 健康日报

默认每天8点运行。
=======
本项目只包含了写月报，周报，日报，代码较为粗糙，大佬勿喷。谢谢，喜欢的话，请给个star
>>>>>>> bb5cf05b55525617681d5cf63d4a66151831c44e

### 命令行运行

```bash
<<<<<<< HEAD
java -jar xixunyun-0.0.1-SNAPSHOT.jar --xixunyun.username=习讯云账号 --xixunyun.password=密码 --pushplus.token=推送token --xixunyun.familyName=紧急联系人姓名 --xixunyun.familyPhone=紧急联系人电话
=======
java -jar java -jar xixunyun-0.0.1-SNAPSHOT.jar --xixunyun.username=习讯云账号 --xixunyun.password=密码 --pushplus.token=推送token
>>>>>>> bb5cf05b55525617681d5cf63d4a66151831c44e
```

### pushplus

pushplus(推送加)是集成了微信、企业微信、钉钉、短信、邮件等渠道的信息推送平台

只需要调用一个简单的API接口，即可帮助你迅速完成消息推送，使用简单方便

我们的所做的一切只是为了让推送变的更简单（复制自<a href= "https://pushplus.plus/">官网</a>）

![image-20220512212156872](https://cdn.jsdelivr.net/gh/2414690715/imgPool/img/2022051261814805c3c2fe472fff1d7d0316ba51-1719b3.png)

微信扫码登录复制你的token

### Docker

![image-20220513121210017](https://cdn.jsdelivr.net/gh/2414690715/imgPool/img/202205130e1bf578e25a836b45f466fcf25dcaea-a5cd17.png)

```bash
把Dockerfile和Jar包单独放到一个文件夹
# cd /opt
# mkdir xixunyun
# cd xixunyun
# docker build -t 镜像名字 .
```

### 参数详解

#### 健康状态

```json
"health_type": 1
```

可填入`1`和`2`两个值，`1`代表健康，`2`代表有**发烧、咳嗽等症状**

#### 健康码

```json
"code": 2
```

可以填入`2`、`3`、`4`三个值 **(没有1)**，分别代表**绿码**，黄码，红码

#### 是否接种疫苗(不包含加强针)

```json
"vaccin": 2
```

可以填入`1`、`2`两个值，分别代表**未接种**和接种

#### 是否接种加强针

```json
"strong": 1
```

可以填入`1`、`2`两个值，分别代表**未接种**和接种

#### 14天内是否去过高风险地区

```json
"high-risk_areas": 0
```

可以填入`0`或`1`两个值，分别代表**没去过高风险地区**和**去过高风险地区**

#### 是否接触过疑似或确诊新冠患者

```json
"ill": 0
```

可以填入`0`或`1`两个值，分别代表**没接触**和**接触**

#### 家庭紧急联系人

```json
"familyName": "xxx",
"familyPhone": "xxx"
```


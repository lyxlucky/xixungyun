package com.lyx.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lyx.entity.*;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liao
 * @date 2022/5/12 17:28
 */
@RestController
public class XixunyunServices {

    private final String LOGIN_URL = "https://api.xixunyun.com/login/admin";

    private String daySignUrl = "https://api.xixunyun.com/Reports/StudentOperator?token={}";

    private String commonSignContent = "[{\"title\":\"实习工作具体情况及实习任务完成情况\",\"content\":\"{}\",\"require\":\"1\",\"sort\":1},{\"title\":\"主要收获及工作成绩\",\"content\":\"{}\",\"require\":\"0\",\"sort\":2},{\"title\":\"工作中的问题及需要老师的指导帮助\",\"content\":\"{}\",\"require\":\"0\",\"sort\":3}]";

    private final String pushPlusUrl = "http://www.pushplus.plus/send";

    private String everyDaySignUrl = "https://api.xixunyun.com/signin_rsa?platform=1&school_id={}&from=app&token={}&version=4.6.22";

    private String everyHealthSignUrl = "https://api.xixunyun.com/health/add?token={}";

    private String refer = "https://www.xixunyun.com/webapp-new/html/health/health.html?token={}&system=ios&time={}&school_id={}&"+ UUID.randomUUID().toString();

    private final String RSA_PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlYsiV3DsG\n" +
            "+t8OFMLyhdmG2P2J4GJwmwb1rKKcDZmTxEphPiYTeFIg4IFEiqDCA\n" +
            "TAPHs8UHypphZTK6LlzANyTzl9LjQS6BYVQk81LhQ29dxyrXgwkRw9RdWa\n" +
            "MPtcXRD4h6ovx6FQjwQlBM5vaHaJOHhEorHOSyd/deTvcS+hRSQIDAQAB";

    @Value("${pushplus.token}")
    private String token;

    @Autowired
    private Xixunyun xixunyun;

    @Autowired
    private DaySign daySignTopic;

    @Autowired
    private MonthSign monthSignTopic;

    @Autowired
    private WeekSign weekSignTopic;

    @Autowired
    private EveryDaySign everyDaySign;

    /**
     * 根据用户名，密码，学校代码，获取token。
     * @return
     */
    public String login(){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("school_id",xixunyun.getSchool());
        map.put("type","web");
        map.put("j_data", StrUtil.format("{};{};1",xixunyun.getUsername(),xixunyun.getPassword()));
        String res = UnicodeUtil.toString(HttpUtil.post(LOGIN_URL, map));
        Map<String, Object> resmap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> dataMap = JSONObject.parseObject(resmap.get("data").toString(), new TypeReference<Map<String, Object>>() {});
        System.out.println("res = " + res);
        return dataMap.get("token").toString();
    }


    @Scheduled(cron = "0 0 20 1/1 1/1 ?")
    public void daySign(){
        String todayAndEndDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        commonSign("day",todayAndEndDay,todayAndEndDay);
    }

    @Scheduled(cron = "0 0 20 1/7 1/1 ?")
    public void weekSign(){
        String weekStartTime = LocalDate.now().with(DayOfWeek.of(1)).atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-","/");
        String weekEndTime = LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonthValue()+"/"+String.valueOf(Integer.parseInt(weekStartTime.split("/")[2]) + 6);
        commonSign("week",weekStartTime,weekEndTime);
    }

    @Scheduled(cron = "0 0 18 20 1/1 ?")
    public void monthSign(){
        Calendar a = Calendar.getInstance();
        //把日期设置为当月第一天
        a.set(Calendar.DATE, 1);
        //日期回滚一天，也就是最后一天
        a.roll(Calendar.DATE, -1);
        //当月有多少天
        int maxDate = a.get(Calendar.DATE);
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy/MM/");
        String monthStartTime = sdfTwo.format(new Date()) + "01";
        String monthEndTime = sdfTwo.format(new Date()) + maxDate;
        commonSign("month",monthStartTime,monthEndTime);
    }

    @Scheduled(cron = "0 0 06 1/1 1/1 ?")
    public void everyDaySign(){
        String token = login();
        Map<String, Object> map = everyDaySignMap(token);
        String res = UnicodeUtil.toString(HttpUtil.post(StrFormatter.format(everyDaySignUrl, xixunyun.getSchool(), token), map));
        Map<String, Object> resMap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        String code = resMap.get("code").toString();
        if("20000".equals(code)){
            Map<String, Object> data = JSONObject.parseObject(resMap.get("data").toString(), new TypeReference<Map<String, Object>>() {});
            System.out.println("data = " + data);
            String message = data.get("message_string").toString();
            push("每日签到结果："+message);
        }
        System.out.println("resMap = " + resMap);
    }

    /**
     * token是登录获取的用户token
     * address 顾名思义url编码
     * latitude 是rsa加密，详情百度
     * longitude 同上
     * origin_latitude&origin_longitude是真实的经纬度，请参考https://lbs.amap.com/tools/picker
     * 其他没什么好说的
     * @param token
     * @return
     */
    public Map<String,Object> everyDaySignMap(String token){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("address",everyDaySign.getAddress());
        map.put("address_name",everyDaySign.getAddressName());
        map.put("change_sign_resource","0");
        map.put("from","app");
        RSA rsa = new RSA(null,RSA_PUBLIC_KEY);
        map.put("latitude",String.valueOf(rsa.encrypt(StrUtil.bytes(everyDaySign.getLatitude()),KeyType.PublicKey)));
        map.put("longitude",String.valueOf(rsa.encrypt(StrUtil.bytes(everyDaySign.getLongitude()),KeyType.PublicKey)));
        map.put("origin_latitude",everyDaySign.getLatitude());
        map.put("origin_longitude",everyDaySign.getLongitude());
        map.put("platform","1");
        map.put("remark","0");
        map.put("school_id",xixunyun.getSchool());
        map.put("sign_type","0");
        map.put("token",token);
        map.put("version","4.6.22");
        return map;
    }


    @Scheduled(cron = "0 0 08 1/1 1/1 ?")
    public void everyHealthSign(){
        String token = login();
        Map<String, Object> map = healthSignMap();
        String res = HttpRequest.post(StrFormatter.format(everyHealthSignUrl, token))
                .header("Referer", StrFormatter.format(refer, token, new SimpleDateFormat("HH:mm:ss").format(new Date()), xixunyun.getSchool()))
                .form(map)
                .execute().body();
        Map<String, Object> resMap = JSONObject.parseObject(UnicodeUtil.toString(res), new TypeReference<Map<String, Object>>() {});
        System.out.println("resMap = " + resMap);
        push("20000".equals(resMap.get("code").toString())?"健康日报填完了":"不知道填完没，请上习讯云查看");
    }

    public Map<String,Object> healthSignMap(){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("health_type","1");
        map.put("province_id","0");
        map.put("city_id","0");
        map.put("district_id","0");
        map.put("hubei","0");
        map.put("ill","0");
        map.put("state","1");
        map.put("code","2");
        map.put("vaccin","2");
        map.put("strong","1");
        map.put("intern_place","0");
        map.put("epidemic","0");
        map.put("vaccine","0");
        map.put("family_name",xixunyun.getFamilyName());
        map.put("family_phone",xixunyun.getFamilyPhone());
        map.put("temperature","36");
        map.put("safe","%5B%5D");
        return map;
    }

    public void commonSign(String type,String startTime,String endTime){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("business_type",type);
        map.put("start_date", startTime);
        map.put("end_date",endTime);
        String value = null;
        if("day".equals(type)){
            value = StrUtil.format(commonSignContent, daySignTopic.getSituation(), daySignTopic.getGrade(), daySignTopic.getHelp());
        }
        if("week".equals(type)){
            value = StrUtil.format(commonSignContent, weekSignTopic.getSituation(), weekSignTopic.getGrade(), weekSignTopic.getHelp());
        }
        if("month".equals(type)){
            value = StrUtil.format(commonSignContent, monthSignTopic.getSituation(), monthSignTopic.getGrade(), monthSignTopic.getHelp());
        }
        map.put("content", value);
        String token = login();
        String res = UnicodeUtil.toString(HttpUtil.post(StrUtil.format(daySignUrl, token), map));
        Map<String, Object> resMap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        String signCode = resMap.get("code").toString();
        switch (type){
            case "day":push("20000".equals(signCode)?"<h3>日签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
            break;
            case "week":push("20000".equals(signCode)?"<h3>周签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
            break;
            case "month":push("20000".equals(signCode)?"<h3>月签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
            break;
            default:return;
        }

    }

    public void push(String content){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("token",token);
        map.put("title","签到结果");
        map.put("content",content);
        String res = HttpUtil.get(pushPlusUrl, map);
        System.out.println("res = " + res);
    }

}

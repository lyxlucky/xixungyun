package com.lyx.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lyx.entity.DaySign;
import com.lyx.entity.MonthSign;
import com.lyx.entity.WeekSign;
import com.lyx.entity.Xixunyun;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    @Scheduled(cron = "0 0 20 1/1 1/1 ? *")
    public void daySign(){
        String todayAndEndDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("business_type","day");
        map.put("start_date", todayAndEndDay);
        map.put("end_date",todayAndEndDay);
        String value = StrUtil.format(commonSignContent, daySignTopic.getSituation(), daySignTopic.getGrade(), daySignTopic.getHelp());
        map.put("content", value);
        String token = login();
        String res = UnicodeUtil.toString(HttpUtil.post(StrUtil.format(daySignUrl, token), map));
        Map<String, Object> resMap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> signMap = JSONObject.parseObject(resMap.get("code").toString(), new TypeReference<Map<String, Object>>() {});
        String signCode = signMap.get("code").toString();
        push("20000".equals(signCode)?"<h3>日签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
    }

    @Scheduled(cron = "0 0 0 1/7 1/1 ? ")
    public void weekSign(){
        String weekStartTime = LocalDate.now().with(DayOfWeek.of(1)).atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-","/");
        String weekEndTime = LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonthValue()+"/"+String.valueOf(Integer.parseInt(weekStartTime.split("/")[2]) + 6);
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("business_type","week");
        map.put("start_date", weekStartTime);
        map.put("end_date",weekEndTime);
        String value = StrUtil.format(commonSignContent, weekSignTopic.getSituation(), weekSignTopic.getGrade(), weekSignTopic.getHelp());
        map.put("content", value);
        String token = login();
        String res = UnicodeUtil.toString(HttpUtil.post(StrUtil.format(daySignUrl, token), map));
        Map<String, Object> resMap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        push("20000".equals(resMap.get("code"))?"<h3>周签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
        System.out.println("res = " + res);
    }

    @Scheduled(cron = "0 0 0 20 1/1 ? ")
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
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("business_type","month");
        map.put("start_date", monthStartTime);
        map.put("end_date",monthEndTime);
        String value = StrUtil.format(commonSignContent, monthSignTopic.getSituation(), monthSignTopic.getGrade(), monthSignTopic.getHelp());
        map.put("content", value);
        String token = login();
        String res = UnicodeUtil.toString(HttpUtil.post(StrUtil.format(daySignUrl, token), map));
        Map<String, Object> resMap = JSONObject.parseObject(res, new TypeReference<Map<String, Object>>() {});
        push("20000".equals(resMap.get("code"))?"<h3>月签成功<h3>":"<h3>不知道成功没，请上习讯云查看<h3>");
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

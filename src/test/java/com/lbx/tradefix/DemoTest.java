package com.lbx.tradefix;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 查组织分片信息
 * @author LiuY
 * @date 2025/1/10
 **/
public class DemoTest {

    public static void main(String[] args) {
        String path = DemoTest.class.getClassLoader().getResource("").getPath();
        // 需要查询ds的组织
        String tagDs = "11949,10252,11823,10200,10771,10570,15374,14670,11568,10307,12471,15839,11593,10267,10314,10328,10289,10299,10258,18591,10188,10238,10241,10291,10295,10301,10596,10823,10824,10909,11107,11109,11181,11832,11853,11952,12271,12527,18593,18621,13818,13824,13825,13943,14245,14987,14990,15348,15821,583883";
        Map<String,String> orgMap = new HashMap<>();
        for(String line : FileUtil.readLines(new File(path+"\\file\\org.txt"),
                Charset.defaultCharset())){
            String[] split = line.split("=");
            orgMap.put(split[0], split[1]);
        }
        Map<String,String> sqlMap = new HashMap<>();
        for(String line : FileUtil.readLines(new File(path+"\\file\\db.txt"),
                Charset.defaultCharset())){
            String[] split = line.split(",");
            sqlMap.put(split[0],split[1]);
        }

        Map<String,String> dsMap = new HashMap<>();
        for(Map.Entry entry:orgMap.entrySet()){
            String value = entry.getValue().toString();
            for (String s : value.split(",")) {
                dsMap.put(s,entry.getKey().toString());
            }
        }
        String[] tagArr = tagDs.split(",");
        for(String tag:tagArr){
            tag = tag.trim();
            if(dsMap.containsKey(tag)){
                System.out.println(tag+","+dsMap.get(tag)+","+sqlMap.get(dsMap.get(tag)));
            }else {
                System.out.println(tag+" not exist");
            }
        }
    }
}

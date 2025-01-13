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
        String tagDs = "10187";
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

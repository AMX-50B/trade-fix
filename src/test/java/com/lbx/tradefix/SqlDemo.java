package com.lbx.tradefix;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查组织分片信息
 * @author LiuY
 * @date 2025/1/10
 **/
public class SqlDemo {

    public static void main(String[] args) {
        String path = SqlDemo.class.getClassLoader().getResource("").getPath();

        Map<String,String> orgMap = new HashMap<>();
        for(String line : FileUtil.readLines(new File(path+"\\file\\org.txt"),
                Charset.defaultCharset())){
            String[] split = line.split("=");
            orgMap.put(split[0], split[1]);
        }
        Map<String,String> dbMap = new HashMap<>();
        for(String line : FileUtil.readLines(new File(path+"\\file\\db.txt"),
                Charset.defaultCharset())){
            String[] split = line.split(",");
            dbMap.put(split[0],split[1]);
        }
        Map<String,String> dsMap = new HashMap<>();
        for(Map.Entry entry:orgMap.entrySet()){
            String value = entry.getValue().toString();
            for (String s : value.split(",")) {
                dsMap.put(s,entry.getKey().toString());
            }
        }
        List<SqlModel>  sqlList= new ArrayList<>();
        for(String line : FileUtil.readLines(new File(path+"\\file\\info1.txt"),
                Charset.defaultCharset())){
            String[] split = line.split("###");
            if("10188".equals(split[0])){
                continue;
            }
            SqlModel sqlModel = new SqlModel();
            sqlModel.setOrg(split[0]);
            sqlModel.setSql(split[1]);
            sqlModel.setDs(dsMap.get(split[0]));
            sqlModel.setUrl(dbMap.get(sqlModel.getDs()));
            sqlList.add(sqlModel);
        }
        List<String> lines = new ArrayList<>();
        Map<String, List<SqlModel>> out = sqlList.stream().collect(Collectors.groupingBy(SqlModel::getUrl));
        for(Map.Entry<String,List<SqlModel>> entry:out.entrySet()){
            lines.add("--ip:"+entry.getKey());
            for(SqlModel sqlModel:entry.getValue()){
                lines.add(sqlModel.getSql());
            }
        }
        File file = new File(path+"\\file\\pay_detail2.txt");
        if(file.exists()){
            file.delete();
        }
        FileUtil.writeLines(lines,file,Charset.defaultCharset());
    }
}

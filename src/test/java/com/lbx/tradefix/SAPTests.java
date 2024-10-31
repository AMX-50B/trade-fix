package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.config.TaskThreadPoolConfig;
import com.lbx.tradefix.service.BaseMessageService;
import com.lbx.tradefix.service.SapService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.OrgInfo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
@Slf4j
public class SAPTests {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    private SapService sapService;
    @Resource
    BaseMessageService baseMessageService;
    @Autowired
    @Qualifier(TaskThreadPoolConfig.EXPORT_EXECUTOR_BEAN_NAME)
    private Executor executor;

    @Test
    public void contextLoads() throws ParseException {
        FixDataQuery vo = new FixDataQuery();
//        vo.setBilldateEnd("2024-01-08");
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> d = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, String> wareMap = baseMessageService.getWareMessage(d);
        for (FixDataVo fix : fixList) {
//            executor.execute(()->{doSave(fix,wareMap);});
            doSave(fix, wareMap);
        }
    }

    private void doSave(FixDataVo fix,Map<String, String> wareMap) {
        SAPInfoQuery query = new SAPInfoQuery();
        query.setGoodsid(wareMap.get(fix.getUdfcode()));
        query.setDeptid(fix.getDeptid());
        try {
            query.setDateUploadStart(DateUtil.getStartTime(fix.getBilldate()));
            query.setDateUploadEnd(DateUtil.getEndTime(fix.getBilldate()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<SAPInfo> info = sapService.getSAPInfo(query);
        if(CollectionUtils.isEmpty(info)){
            log.info("同步:{},条目:{}",fix.getLine(),0);
            return;
        }
        for(SAPInfo sapInfo : info) {
            sapInfo.setLine(fix.getLine());
            sapInfo.setDeptid(fix.getDeptid());
        }
        int i = tradeFixService.insertSapData(info);
        log.info("同步:{},条目:{}，{}",fix.getLine(),info.size(),i);
    }

}

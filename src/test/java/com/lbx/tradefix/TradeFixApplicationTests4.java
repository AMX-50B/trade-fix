package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.service.*;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.OrderQuery;
import com.lbx.tradefix.vo.query.WareInfoQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTests4 {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTests2.class);

    @Resource
    TradeFixService tradeFixService;
    @Autowired
    BaseMessageService baseMessageService;

    @Test
    public void contextLoads() throws ParseException {
        ReportVo query = new ReportVo();
//        query.setStatus(-11);
       List<ReportVo> vos = tradeFixService.getReport(query);
        Set<Long> codes = vos.stream().map(ReportVo::getWareInsideCode).collect(Collectors.toSet());

        Map<Long, WareInfoVo> map = baseMessageService.getWareMessageByInsideCode(codes);
        for(ReportVo vo:vos){
            WareInfoVo wareInfoVo = map.get(vo.getWareInsideCode());
            if(wareInfoVo!=null){
               vo.setPrice(wareInfoVo.getPriceMarket());
               tradeFixService.updateReport(vo);
           }
       }
    }

}

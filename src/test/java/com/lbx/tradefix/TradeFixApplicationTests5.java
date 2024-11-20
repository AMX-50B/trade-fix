package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.service.OrderInfoService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
@Slf4j
public class TradeFixApplicationTests5 {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    OrderInfoService orderInfoService;

    @Test
    public void contextLoads() {
        ReportVo vo = new ReportVo();
        vo.setStatus(-11);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        int i = 0;
        List<ReportVo> report = tradeFixService.getReport(vo);
        for (ReportVo reportVo : report) {
            OrderQuery query = new OrderQuery();
            query.setCompanyId(reportVo.getCompanyId());
            query.setBusinessId(reportVo.getBusinessId());
            query.setBillNum(reportVo.getBillNo().toString());
            List<OrderOutBoundVo> crmData = orderInfoService.getCRMData(query);
            if(!CollectionUtils.isEmpty(crmData)){
                i++;
                log.info("查到积分兑换数据：{}",reportVo.getBillNo());
            }
        }
        log.info("共{}条，查到CRM：{}条",report.size(),i);

    }

}

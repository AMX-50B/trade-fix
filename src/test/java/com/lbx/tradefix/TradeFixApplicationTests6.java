package com.lbx.tradefix;


import com.lbx.tradefix.service.BaseMessageService;
import com.lbx.tradefix.service.OrderInfoService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.WareInfoVo;
import com.lbx.tradefix.vo.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
@Slf4j
public class TradeFixApplicationTests6 {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;

    @Test
    public void contextLoads() {
        ReportVo vo = new ReportVo();
//        vo.setStatus(-11);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        int i = 0;
        List<ReportVo> report = tradeFixService.getReport(vo);
        for (ReportVo reportVo : report) {
            if(!StringUtils.isEmpty(reportVo.getWareInsideCode1())){
                WareInfoVo wareInfoVo = baseMessageService.getStoreWareMessageByInsideCode(new Long(reportVo.getWareInsideCode1()), reportVo.getBusinessId());
                if (wareInfoVo != null) {
                    double v = Math.round(wareInfoVo.getMovingAveragePrice() * 100.0) / 100.0;
                    reportVo.setPrice1(v);
                }
            }
            if(!StringUtils.isEmpty(reportVo.getWareInsideCode2())){
                WareInfoVo wareInfoVo = baseMessageService.getStoreWareMessageByInsideCode(new Long(reportVo.getWareInsideCode2()), reportVo.getBusinessId());
                if (wareInfoVo != null) {
                    double v = Math.round(wareInfoVo.getMovingAveragePrice() * 100.0) / 100.0;
                    reportVo.setPrice2(v);
                }
            }
            if(reportVo.getPrice1()!=null || reportVo.getPrice2()!=null){
                tradeFixService.updateReport(reportVo);
            }
        }
    }

}

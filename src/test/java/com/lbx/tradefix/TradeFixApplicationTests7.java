package com.lbx.tradefix;


import com.lbx.tradefix.service.BaseMessageService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.OrgInfo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.WareInfoVo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
@Slf4j
public class TradeFixApplicationTests7 {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;

    @Test
    public void contextLoads() {
        FixDataQuery vo = new FixDataQuery();
//        vo.setBilldate("2024-07-30");
//        vo.setLine(84626L);
        vo.setErpNum(0d);
        vo.setStatus(-2);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> d = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, OrgInfo> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(d);
        for (FixDataVo fixDataVo : fixList) {
            Long wareInsideCode = Long.parseLong(wareMap.get(fixDataVo.getUdfcode()));
            WareInfoVo wareInfoVo = baseMessageService.getStoreWareMessageByInsideCode(wareInsideCode, orgMap.get(fixDataVo.getDeptid()).getId());
            if (wareInfoVo != null) {
                double v = Math.round(wareInfoVo.getMovingAveragePrice() * 100.0) / 100.0;
                fixDataVo.setDiffNum(v);
            }
            tradeFixService.update(fixDataVo);
        }
    }

}

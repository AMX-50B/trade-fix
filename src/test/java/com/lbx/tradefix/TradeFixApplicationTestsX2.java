package com.lbx.tradefix;


import com.lbx.tradefix.service.*;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.OrderQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTestsX2 {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTestsX2.class);
    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Resource
    SapService sapService;

    @Resource
    StockService stockService;

    @Test
    public void contextLoads() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FixDataQuery vo = new FixDataQuery();
        vo.setStatus(0);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> udfCode = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, OrgInfo> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(udfCode);
        for (FixDataVo fix : fixList) {
            try {
                SAPInfoQuery query = new SAPInfoQuery();
                query.setGoodsid(wareMap.get(fix.getUdfcode()));
                query.setDeptid(fix.getDeptid());
                query.setDateUploadStart(DateUtil.getStartTime(fix.getBilldate()));
                query.setDateUploadEnd(DateUtil.getEndTime(fix.getBilldate()));
                List<SAPInfo> infos = sapService.getSAPInfo2(query);
                if(CollectionUtils.isEmpty(infos)){
                    log.info("[日期不对] fix:{}", fix);
                    fix.setStatus(9);
                    tradeFixService.updateFixDataVo(fix);
                    continue;
                }
                for (SAPInfo sapInfo : infos) {
                    OrderQuery orderQuery = new OrderQuery();
                    orderQuery.setGroupId(10000L);
                    orderQuery.setCompanyId(orgMap.get(fix.getDeptid()).getParentOrgId());
                    orderQuery.setBusinessId(orgMap.get(fix.getDeptid()).getId());
                    orderQuery.setWareInsideCode(Long.parseLong(wareMap.get(fix.getUdfcode())));
                    orderQuery.setBillNumber(String.valueOf(sapInfo.getPbseqid()));
                    orderQuery.setBillOperateTimeStart(DateUtil.getStartTime(fix.getBilldate()));
                    orderQuery.setBillOperateTimeEnd(DateUtil.getEndTime(fix.getBilldate()));
                    StockEntity stockEntity = stockService.getOrderData2(orderQuery);
                    if (stockEntity == null){
                        System.out.println("[erp未扣减库存] " + sapInfo + ",sap数量:" + sapInfo.getTotal());
                        stockEntity = new StockEntity();
                        stockEntity.setLine(fix.getLine().intValue());
                        stockEntity.setGroupId(10000L);
                        stockEntity.setCompanyId(orderQuery.getCompanyId());
                        stockEntity.setBusinessId(orderQuery.getBusinessId());
                        stockEntity.setBillNumber(sapInfo.getPbseqid().toString());
                        stockEntity.setBillType(sapInfo.getFgtyp() == null ? "0" : sapInfo.getFgtyp().toString());
                        stockEntity.setChangeStockQty(0.0);
                        stockEntity.setSapNum(sapInfo.getTotal());
                        stockEntity.setWareInsideCode(wareMap.get(fix.getUdfcode()));
                        stockEntity.setBillOperateTime(simpleDateFormat.parse(fix.getBilldate()));
                        tradeFixService.saveResult(stockEntity);
                        continue;
                    }
                    stockEntity.setBillOperateTime(simpleDateFormat.parse(fix.getBilldate()));
                    stockEntity.setSapNum(sapInfo.getTotal());
                    stockEntity.setLine(fix.getLine().intValue());
                    if (Math.abs(stockEntity.getChangeStockQty()) != Math.abs(stockEntity.getSapNum())) {
                        tradeFixService.saveResult(stockEntity);
                        System.out.println("[erp-sap数据对不上] " + stockEntity + "erp数量:" + stockEntity.getChangeStockQty() + ",sap数量:" + stockEntity.getSapNum());
                    } else {
                        tradeFixService.saveResult(stockEntity);
                        System.out.println("[erp-sap数据一致] " + stockEntity + "erp数量:" + stockEntity.getChangeStockQty() + ",sap数量:" + stockEntity.getSapNum());
                    }
                }

            } catch (Exception e) {
                log.error("line:{} -> 处理失败：{}", fix.getLine(), e.getMessage());
                throw e;
            }
        }
    }
}

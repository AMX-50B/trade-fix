package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.config.BizException;
import com.lbx.tradefix.config.TaskThreadPoolConfig;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTestsX {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTestsX.class);
    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Resource
    SapService sapService;
    @Autowired
    private OrderOutBoundService outBoundService;
    @Autowired
    private StockService stockService;

    @Test
    public void contextLoads() throws ParseException {
        FixDataQuery vo = new FixDataQuery();
        vo.setErpNum(0d);
        vo.setStatus(0);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> udfCode = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, OrgInfo> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(udfCode);
        for (FixDataVo fix : fixList) {
            try {
                OrgInfo orgInfo = orgMap.get(fix.getDeptid());
                String goodsId = wareMap.get(fix.getUdfcode());
                OrderQuery orderQuery = new OrderQuery();
                orderQuery.setGroupId(10000L);
                orderQuery.setCompanyId(orgInfo.getParentOrgId());
                orderQuery.setBusinessId(orgInfo.getId());
                orderQuery.setWareInsideCode(Long.parseLong(goodsId));
                orderQuery.setBillOperateTimeStart(DateUtil.getStartTime(fix.getBilldate()));
                orderQuery.setBillOperateTimeEnd(DateUtil.getEndTime(fix.getBilldate()));
                List<StockEntity> orderData = stockService.getOrderData(orderQuery);
                if (CollectionUtils.isEmpty(orderData)){
                    fix.setStatus(9);
                    tradeFixService.updateFixDataVo(fix);
                    log.info("[日期不对] fix:{}", fix);
                    continue;
                }
                for (StockEntity stockEntity : orderData) {
                    stockEntity.setLine(fix.getLine().intValue());
                    stockEntity.setStatus(1);
                    if ("24".equals(stockEntity.getBillType())) { //销单
                        OrderBoundQuery query = new OrderBoundQuery();
                        query.setBillNo(stockEntity.getBillNumber());
                        query.setBillType(1);
                        OrderOutBoundVo outbound = outBoundService.getOutbound(query);
                        if (outbound == null) {
                            stockEntity.setStatus(0);
                            tradeFixService.saveResult(stockEntity);
                            System.out.println("[凌云单据未出库]-" + stockEntity);
                            continue;
                        }
                        stockEntity.setOrderId(outbound.getOrderId());
                        stockEntity.setBillNumber(outbound.getOrderId());
                        handle(outbound.getOrderId(), stockEntity);
                    } else if("108".equals(stockEntity.getBillType())){   //促销赠品
                        OrderBoundQuery query = new OrderBoundQuery();
                        query.setBillNo(stockEntity.getBillNumber());
                        query.setBillType(2);
                        OrderOutBoundVo outbound = outBoundService.getOutbound(query);
                        if (outbound == null) {
                            stockEntity.setStatus(0);
                            tradeFixService.saveResult(stockEntity);
                            System.out.println("[凌云单据未出库]-" + stockEntity);
                            continue;
                        }
                        stockEntity.setOrderId(outbound.getOrderId());
                        String billNo = stockService.selectBillNoByOrderId(Long.valueOf(outbound.getOrderId()), Long.valueOf(stockEntity.getWareInsideCode()));
                        stockEntity.setBillNumber(billNo);
                        handle(billNo, stockEntity);
                    }else if("51".equals(stockEntity.getBillType())){     //积分商城
                        handle(stockEntity.getBillNumber(), stockEntity);
                    }else if("52".equals(stockEntity.getBillType())){                                               //赠品出库单
                        handle(stockEntity.getBillNumber(), stockEntity);
                    }else {
                        System.out.println("未找到的单据类型");
                    }
                }
            } catch (Exception e) {
                log.error("line:{} -> 处理失败：{}", fix.getLine(), e.getMessage());
                throw e;
            }
        }
    }

    public void handle(String billNumber, StockEntity stockEntity){
        SAPInfoQuery sapInfoQuery = new SAPInfoQuery();
        sapInfoQuery.setBillNo(Long.parseLong(billNumber));
        sapInfoQuery.setGoodsId(Long.parseLong(stockEntity.getWareInsideCode()));

        Double total = sapService.getSAPInfo(sapInfoQuery);

        if (total == null) {
            stockEntity.setSapNum(0.0);
            tradeFixService.saveResult(stockEntity);
            System.out.println("[sap未过账]-" + stockEntity);
        } else {
            stockEntity.setSapNum(total);
            if (Math.abs(total) != Math.abs(stockEntity.getChangeStockQty())) {
                tradeFixService.saveResult(stockEntity);
                System.out.println("[erp-sap数据对不上] " + stockEntity + "erp数量:" + stockEntity.getChangeStockQty() + ",sap数量:" + total);
            } else {
                tradeFixService.saveResult(stockEntity);
                System.out.println("[erp-sap数据一致] " + stockEntity + "erp数量:" + stockEntity.getChangeStockQty() + ",sap数量:" + total);
            }
        }
    }
}

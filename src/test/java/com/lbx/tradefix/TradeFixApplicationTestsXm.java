package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.config.BizException;
import com.lbx.tradefix.config.TaskThreadPoolConfig;
import com.lbx.tradefix.dao.TradeFixDao;
import com.lbx.tradefix.dao.TradeTiDBDao;
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
public class TradeFixApplicationTestsXm {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTests2.class);

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Autowired
    private TradeTiDBDao tiDBDao;
    @Autowired
    private TradeFixDao tradeFixDao;
    @Autowired
    private TradeTiDBDao tradeTiDBDao;
    @Autowired
    private SapService sapService;
    @Test
    public void contextLoads() throws ParseException {

        FixDataQuery vo = new FixDataQuery();
        vo.setStatus(19);
//        vo.setLine(95648L);
        List<FixDataVo> fixList = tradeFixService.getXmTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> d = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, OrgInfo> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(d);
        List<FixResultStockLy> stocks = new ArrayList<>();
        for (FixDataVo fix : fixList) {
            boolean flag = false;
            try {
                String goodsid = wareMap.get(fix.getUdfcode());
                OrgInfo business = orgMap.get(fix.getDeptid());
                if(StringUtils.isEmpty(goodsid)||business==null){
                    throw BizException.builder().code(-1).msg("商品编码:{}或组织:{}查不到").detail(fix.getUdfcode(),fix.getDeptid()).build();
                }
//                String convertId = goodsIdConvertMap.get(goodsid);
//                if(!StringUtils.isEmpty(convertId)){
//                    goodsid = convertId;
//                    flag = true;
//                }
                OrderQuery query = new OrderQuery();
                query.setOrderStartDate(DateUtil.getStartTime(fix.getBilldate()));
                query.setOrderEndDate(DateUtil.getEndTime(fix.getBilldate()));
                query.setGroupId(10000L);
                query.setBusinessId(business.getId());
                query.setCompanyId(business.getParentOrgId());
                query.setWareInsideCode(Long.parseLong(goodsid));
                List<OrderOutBoundVo> orderBill = tiDBDao.selectOrderDataByCondition(query);
                if (!CollectionUtils.isEmpty(orderBill)) {
                    flag = true;
                    Map<Long, DoubleSummaryStatistics> collect = orderBill.stream().collect(Collectors.groupingBy(OrderOutBoundVo::getId,
                            Collectors.summarizingDouble(OrderOutBoundVo::getWareQty)));

                    for (Long orderId : collect.keySet()) {
                        FixResultStockLy stock = new FixResultStockLy();
                        stock.setStatus(9);
                        stock.setBillType("24");
                        stock.setBusinessId(query.getBusinessId());
                        stock.setCompanyId(query.getCompanyId());
                        stock.setLine(fix.getLine().intValue());
                        stock.setWareInsideCode(goodsid);
                        stock.setBillOperateTime(DateUtil.parseDate(fix.getBilldate()));
                        stock.setOrderId(orderId.toString());
                        stock.setOriginNum(collect.get(orderId).getSum());
                        stocks.add(stock);
                    }
                    log.info("line:{},检索到订单数据：{}",fix.getLine(),orderBill.size());
                }
                List<OrderOutBoundVo> giftBill = tiDBDao.selectGiftDataByCondition(query);
                if (!CollectionUtils.isEmpty(giftBill)) {
                    flag = true;
                    Map<Long, DoubleSummaryStatistics> collect = giftBill.stream().collect(Collectors.groupingBy(OrderOutBoundVo::getId, Collectors.summarizingDouble(OrderOutBoundVo::getWareQty)));

                    for (Long billNum : collect.keySet()) {
                        FixResultStockLy stock = new FixResultStockLy();
                        stock.setStatus(9);
                        stock.setBillType("52");
                        stock.setBusinessId(query.getBusinessId());
                        stock.setCompanyId(query.getCompanyId());
                        stock.setLine(fix.getLine().intValue());
                        stock.setWareInsideCode(goodsid);
                        stock.setBillOperateTime(DateUtil.parseDate(fix.getBilldate()));
                        stock.setBillNumber(billNum.toString());
                        stock.setOriginNum(collect.get(billNum).getSum());
                        stocks.add(stock);
                    }
                    log.info("line:{},检索到赠品数据：{}",fix.getLine(),giftBill.size());
                }
                List<OrderOutBoundVo> promotionBill = tiDBDao.selectPromotionDataByCondition(query);
                if (!CollectionUtils.isEmpty(promotionBill)) {
                    flag = true;
                    Map<Long, List<OrderOutBoundVo>> collect = promotionBill.stream().collect(Collectors.groupingBy(OrderOutBoundVo::getId));
                    for (Long billNum : collect.keySet()) {
                        FixResultStockLy stock = new FixResultStockLy();
                        stock.setStatus(9);
                        stock.setBillType("108");
                        stock.setBusinessId(query.getBusinessId());
                        stock.setCompanyId(query.getCompanyId());
                        stock.setLine(fix.getLine().intValue());
                        stock.setWareInsideCode(goodsid);
                        stock.setBillOperateTime(DateUtil.parseDate(fix.getBilldate()));
                        stock.setBillNumber(billNum.toString());
                        List<OrderOutBoundVo> orderOutBoundVos = collect.get(billNum);
                        List<Long> orderIds = orderOutBoundVos.stream().map(OrderOutBoundVo::getOrderId).distinct().collect(Collectors.toList());
                        double sum = 0d;
                        for(OrderOutBoundVo orderOutBoundVo : orderOutBoundVos){
                            sum+=orderOutBoundVo.getWareQty();
                        }
                        stock.setOriginNum(sum);
                        stock.setOrderIdList(orderIds);
                        stocks.add(stock);
                    }
                    log.info("line:{},检索到促销数据：{}",fix.getLine(),promotionBill.size());
                }
                List<OrderOutBoundVo> crmBill = tiDBDao.selectCrmDataByCondition(query);
                if (!CollectionUtils.isEmpty(crmBill)) {
                    flag = true;
                    Map<Long, DoubleSummaryStatistics> collect = crmBill.stream().collect(Collectors.groupingBy(OrderOutBoundVo::getId, Collectors.summarizingDouble(OrderOutBoundVo::getWareQty)));
                    for (Long billNum : collect.keySet()) {
                        FixResultStockLy stock = new FixResultStockLy();
                        stock.setStatus(9);
                        stock.setBusinessId(query.getBusinessId());
                        stock.setCompanyId(query.getCompanyId());
                        stock.setLine(fix.getLine().intValue());
                        stock.setBillType("51");
                        stock.setWareInsideCode(goodsid);
                        stock.setBillOperateTime(DateUtil.parseDate(fix.getBilldate()));
                        stock.setBillNumber(billNum.toString());
                        stock.setOriginNum(collect.get(billNum).getSum());
                        stocks.add(stock);
                    }
                    log.info("line:{},检索到CRM数据：{}",fix.getLine(),crmBill.size());
                }
                if(!flag){
                    log.info("line:{},未检索到数据",fix.getLine());
                }

            } catch (Exception e){
                    log.error("line:{} -> 处理失败：{}",fix.getLine(),e.getMessage());
                    throw e;
                }
            }

            for (FixResultStockLy stock : stocks) {
                OrderQuery query = new OrderQuery();
                query.setBusinessId(stock.getBusinessId());
                query.setCompanyId(stock.getCompanyId());
                long billNum;
                if("24".equals(stock.getBillType())){
                    OrderBoundQuery orderBoundQuery = new OrderBoundQuery();
                    orderBoundQuery.setBusinessId(query.getBusinessId());
                    orderBoundQuery.setCompanyId(query.getCompanyId());
                    orderBoundQuery.setOrderId(Long.parseLong(stock.getOrderId()));
                    orderBoundQuery.setBillType(2);
                    OrderOutBoundVo outboundMain = tradeTiDBDao.getOutboundMain(orderBoundQuery);
                    if(outboundMain!=null){
                        query.setId(outboundMain.getId());
                    }
                    billNum = Long.parseLong(stock.getOrderId());
                }else if("108".equals(stock.getBillType())){
                    OrderBoundQuery orderBoundQuery = new OrderBoundQuery();
                    orderBoundQuery.setBusinessId(query.getBusinessId());
                    orderBoundQuery.setCompanyId(query.getCompanyId());
                    orderBoundQuery.setOrderIdList(stock.getOrderIdList());
                    orderBoundQuery.setBillType(2);
                    List<OrderOutBoundVo> outboundMain = tradeTiDBDao.getOutboundMainBatch(orderBoundQuery);
                    if(!CollectionUtils.isEmpty(outboundMain)){
                        List<Long> ids = outboundMain.stream().map(OrderOutBoundVo::getId).distinct().collect(Collectors.toList());
                        query.setIds(ids);
                        stock.setRemark(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
                    }
                    billNum = stock.getId();
                }else {
                    query.setId(Long.parseLong(stock.getBillNumber()));
                    billNum = Long.parseLong(stock.getBillNumber());
                }

                List<OrderOutBoundVo> outBoundVos = tradeTiDBDao.selectStockPurchaseSaleLog(query);
                if (!CollectionUtils.isEmpty(outBoundVos)) {
                    OrderOutBoundVo boundVo = outBoundVos.get(0);
                    stock.setStockDate(boundVo.getCreateTime());
                    double num = 0d;
                    for (OrderOutBoundVo order : outBoundVos) {
                        if(stock.getWareInsideCode().equals(order.getWareInsideCode().toString())){
                            num += order.getWareQty();
                        }
                    }
                    stock.setChangeStockQty(num);
                    log.info("line:{},查到库存数量：{}",stock.getLine(),num);
                }
                List<SAPInfo> sapDetail = sapService.getSAPDetail(billNum);
                if (!CollectionUtils.isEmpty(sapDetail)) {
                    SAPInfo info = sapDetail.get(0);
                    stock.setStockDate(info.getDateupload());
                    double num = 0d;
                    for (SAPInfo sap : sapDetail) {
                        if(stock.getWareInsideCode().equals(sap.getGoodsid())){
                            num += sap.getTotal();
                        }
                    }
                    stock.setSapNum(num);
                    log.info("line:{},查到SAP数量：{}",stock.getLine(),num);
                }
            }
            tradeFixDao.insertFixResultStockLy(stocks);
    }



}

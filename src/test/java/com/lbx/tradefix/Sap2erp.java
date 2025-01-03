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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class Sap2erp {

    private static final Logger log = LoggerFactory.getLogger(Sap2erp.class);
    @Resource
    TradeFixService tradeFixService;
    @Resource
    OrderService orderService;
    @Resource
    StockService stockService;
    @Resource
    OrderOutBoundService outBoundService;


    @Test
    public void contextLoads() {
        List<StockEntity> stockEntities = tradeFixService.list2();
        for (StockEntity stockEntity : stockEntities) {
            Long groupId = 10000L;
            Long companyId = stockEntity.getCompanyId();
            Long businessId = stockEntity.getBusinessId();
            String billNumber = stockEntity.getBillNumber();
            String wareInsideCode = stockEntity.getWareInsideCode();
            if (stockEntity.getBillType().equals("51")) {  //  积分商城虚拟兑换  1
                Double originNum = orderService.selectJF(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[积分商城] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum != null) {
                    tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
                    if (stockEntity.getBillType().equals("4")) {
                        stockEntity.setBillType("51");
                        tradeFixService.updateBillType(stockEntity);
                    }
                } else {
                    tradeFixService.updateOriginNumById(stockEntity.getId(), 0D);
                }
            } else if (stockEntity.getBillType().equals("52")   || stockEntity.getBillType().equals("4") ) {  //其他赠品出库  1 4
                Double originNum = orderService.selectOtherGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[其他赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null) {
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            } else if (stockEntity.getBillType().equals("0")) {//销售订单  1
                Double originNum = orderService.selectOrder(groupId, companyId, businessId, Long.valueOf(billNumber), Long.valueOf(wareInsideCode));
//                log.info("[销售订单] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum != null) {
                    tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
                    stockEntity.setBillType("24");
                    tradeFixService.updateBillType(stockEntity);

                    //通过订单id找到出库id
                    OrderBoundQuery query = new OrderBoundQuery();
                    query.setGroupId(groupId);
                    query.setCompanyId(companyId);
                    query.setBusinessId(businessId);
                    query.setOrderId(billNumber);
                    query.setBillType(1);
                    Long outboundId = outBoundService.getOutboundId(query);
                    if (outboundId != null) {
                        //通过出库id找到库存出库数量
                        OrderQuery orderQuery = new OrderQuery();
                        orderQuery.setGroupId(groupId);
                        orderQuery.setCompanyId(companyId);
                        orderQuery.setBusinessId(businessId);
                        orderQuery.setBillNumber(String.valueOf(outboundId));
                        orderQuery.setWareInsideCode(Long.parseLong(wareInsideCode));
                        orderQuery.setBillType("24");
                        double num = stockService.selectNum(orderQuery);

                        //修改出库数量
                        stockEntity.setChangeStockQty(num);
                        tradeFixService.updateChangeStockQty(stockEntity);
                    }
                } else {
                    tradeFixService.updateOriginNumById(stockEntity.getId(), 0D);
                }
            } else if (stockEntity.getBillType().equals("1")) { //促销赠品&其他赠品  1
                Double originNum = orderService.selectOtherGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[其他赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum != null) {
                    tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
                    stockEntity.setBillType("52");
                    tradeFixService.updateBillType(stockEntity);
                } else {
                    originNum = orderService.selectPromotionGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                    log.info("[促销赠品] billNumber:{},originNum:{}", billNumber, originNum );
                    if (originNum != null) {
                        tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
                        stockEntity.setBillType("108");
                        tradeFixService.updateBillType(stockEntity);

                        Long orderId = stockService.selectOrderIdByBillNo(stockEntity.getBillNumber(), Long.valueOf(stockEntity.getWareInsideCode()));

                        //通过订单id找到出库id
                        OrderBoundQuery query = new OrderBoundQuery();
                        query.setGroupId(groupId);
                        query.setCompanyId(companyId);
                        query.setBusinessId(businessId);
                        query.setOrderId(String.valueOf(orderId));
                        query.setBillType(2);
                        Long outboundId = outBoundService.getOutboundId(query);
                        if (outboundId != null) {
                            //通过出库id找到库存出库数量
                            OrderQuery orderQuery = new OrderQuery();
                            orderQuery.setGroupId(groupId);
                            orderQuery.setCompanyId(companyId);
                            orderQuery.setBusinessId(businessId);
                            orderQuery.setBillNumber(String.valueOf(outboundId));
                            orderQuery.setWareInsideCode(Long.parseLong(wareInsideCode));
                            orderQuery.setBillType("108");
                            double num = stockService.selectNum(orderQuery);

                            //修改出库数量
                            stockEntity.setChangeStockQty(num);
                            stockEntity.setOrderId(String.valueOf(orderId));
                            tradeFixService.updateChangeStockQty(stockEntity);
                            tradeFixService.updateOrderId(stockEntity);
                        }
                    } else {
                        tradeFixService.updateOriginNumById(stockEntity.getId(), 0D);
                    }
                }
            } else if (stockEntity.getBillType().equals("24")) {//销售订单
                Double originNum = orderService.selectOrder(groupId, companyId, businessId, Long.valueOf(billNumber), Long.valueOf(wareInsideCode));
//                log.info("[销售订单] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null) {
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            } else if (stockEntity.getBillType().equals("108")) {  //促销赠品
                Double originNum = orderService.selectPromotionGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[促销赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null) {
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            } else {
                System.out.println("未知类型");
            }
        }
    }

    @Test
    public void findCodeError() {
        List<StockEntity> stockEntities = tradeFixService.selectByOriginNum(0.0);
        for (StockEntity stockEntity : stockEntities) {
            Long groupId = 10000L;
            Long companyId = stockEntity.getCompanyId();
            Long businessId = stockEntity.getBusinessId();
            String billNumber = stockEntity.getBillNumber();
            if (stockEntity.getBillType().equals("51") || stockEntity.getBillType().equals("4")) {  //  积分商城虚拟兑换  1
                int count = orderService.selectJF2(groupId, companyId, businessId, billNumber);
//                log.info("[积分商城] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                    if (stockEntity.getBillType().equals("4")){
                        stockEntity.setBillType("51");
                        tradeFixService.updateBillType(stockEntity);
                    }
                }
            } else if (stockEntity.getBillType().equals("52")) {  //其他赠品出库  1
                int count = orderService.selectOtherGift2(groupId, companyId, businessId, billNumber);
//                log.info("[其他赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                }
            } else if (stockEntity.getBillType().equals("0")) {//销售订单  1
                int count = orderService.selectOrder2(groupId, companyId, businessId, Long.valueOf(billNumber));
//                log.info("[销售订单] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                    stockEntity.setBillType("24");
                    tradeFixService.updateBillType(stockEntity);
                }
            } else if (stockEntity.getBillType().equals("1")) { //促销赠品&其他赠品  1
                int count = orderService.selectOtherGift2(groupId, companyId, businessId, billNumber);
//                log.info("[其他赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                    stockEntity.setBillType("52");
                    tradeFixService.updateBillType(stockEntity);
                } else {
                    count = orderService.selectPromotionGift2(groupId, companyId, businessId, billNumber);
//                    log.info("[促销赠品] billNumber:{},originNum:{}", billNumber, originNum );
                    if (count > 0) {
                        stockEntity.setStatus(4);
                        tradeFixService.updateStatue(stockEntity);
                        stockEntity.setBillType("108");
                        tradeFixService.updateBillType(stockEntity);
                    }
                }
            } else if (stockEntity.getBillType().equals("24")) {//销售订单
                int count = orderService.selectOrder2(groupId, companyId, businessId, Long.valueOf(billNumber));
//                log.info("[销售订单] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                }
            } else if (stockEntity.getBillType().equals("108")) {  //促销赠品
                int count = orderService.selectPromotionGift2(groupId, companyId, businessId, billNumber);
//                log.info("[促销赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (count > 0) {
                    stockEntity.setStatus(4);
                    tradeFixService.updateStatue(stockEntity);
                }
            } else {
                System.out.println("未知类型");
            }
        }
    }

    @Test
    public void calPrice() {
        List<StockEntity> stockEntities = tradeFixService.list2();
        for (StockEntity stockEntity : stockEntities) {
            Long businessId = stockEntity.getBusinessId();
            String wareInsideCode = stockEntity.getWareInsideCode();
            Double price = orderService.selectPrice(businessId, Long.valueOf(wareInsideCode));
            stockEntity.setPrice(price);
            tradeFixService.updatePrice(stockEntity);
        }
    }
}

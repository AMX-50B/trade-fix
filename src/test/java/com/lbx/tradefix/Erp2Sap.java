package com.lbx.tradefix;

import com.lbx.tradefix.service.OrderService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.StockEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class Erp2Sap {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    OrderService orderService;

    @Test
    public void test(){
        List<StockEntity> stockEntities = tradeFixService.list3();
        for (StockEntity stockEntity : stockEntities) {
            Long groupId = 10000L;
            Long companyId = stockEntity.getCompanyId();
            Long businessId = stockEntity.getBusinessId();
            String billNumber = stockEntity.getBillNumber();
            String orderId = stockEntity.getOrderId();
            String wareInsideCode = stockEntity.getWareInsideCode();
            if (stockEntity.getBillType().equals("51")) {  //  积分商城虚拟兑换  1
                Double originNum = orderService.selectJF(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[积分商城] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null){
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            }else if (stockEntity.getBillType().equals("52")){  //其他赠品出库  1
                Double originNum = orderService.selectOtherGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[其他赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null){
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            }else if (stockEntity.getBillType().equals("24")){//销售订单
                Double originNum = orderService.selectOrder(groupId, companyId, businessId, Long.valueOf(orderId), Long.valueOf(wareInsideCode));
//                log.info("[销售订单] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null){
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            }else if (stockEntity.getBillType().equals("108")) {  //促销赠品
                Double originNum = orderService.selectPromotionGift(groupId, companyId, businessId, billNumber, Long.valueOf(wareInsideCode));
//                log.info("[促销赠品] billNumber:{},originNum:{}", billNumber, originNum );
                if (originNum == null){
                    originNum = 0D;
                }
                tradeFixService.updateOriginNumById(stockEntity.getId(), originNum);
            }else {
                System.out.println("未知类型");
            }
        }
    }
}

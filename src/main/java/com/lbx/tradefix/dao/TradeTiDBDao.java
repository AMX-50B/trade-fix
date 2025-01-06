package com.lbx.tradefix.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.OrderSapMqVo;
import com.lbx.tradefix.vo.OutboundDetail;
import com.lbx.tradefix.vo.StockEntity;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.OrderQuery;
import com.lbx.tradefix.vo.query.OrderSapMqQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@DS("tidb")
public interface TradeTiDBDao {

    OrderOutBoundVo selectOrderOutBound(OrderBoundQuery query);

    List<OrderSapMqVo> selectOrderSapMq(OrderSapMqQuery query);

    OrderOutBoundVo getOutboundMain(OrderBoundQuery query);

    List<OrderOutBoundVo> getOutboundDetail(@Param("outboundId") Long outboundId, @Param("wareInsideCode")Long wareInsideCode);

    List<StockEntity> selectOrderData(OrderQuery query);

    StockEntity selectOrderData2(OrderQuery query);
    List<StockEntity> selectOrderDataFromOrder(OrderQuery query);

    List<OrderOutBoundVo> selectGiftData(OrderQuery query);

    List<OrderOutBoundVo> selectPromotionData(OrderQuery query);

<<<<<<< HEAD
    Double selectJF(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("convertCode") String convertCode, @Param("wareInsideCode") Long wareInsideCode );
    int selectJF2(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("convertCode") String convertCode);

    Double selectOtherGift(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("billNo") String billNo, @Param("wareInsideCode") Long wareInsideCode);
    int selectOtherGift2(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("billNo") String billNo);

    Double selectOrder(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("orderId") Long orderId, @Param("wareInsideCode") Long wareInsideCode);
    int selectOrder2(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("orderId") Long orderId);

    Double selectPromotionGift(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("billNo") String billNo, @Param("wareInsideCode") Long wareInsideCode);
    int selectPromotionGift2(@Param("groupId") Long groupId, @Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("billNo") String billNo);

    Double selectPrice(@Param("businessId") Long businessId, @Param("wareInsideCode") Long wareInsideCode);

    Long getOutboundId(OrderBoundQuery query);

    Double selectNum(OrderQuery orderQuery);

    String selectBillNoByOrderId(@Param("orderId") Long orderId, @Param("wareInsideCode") Long wareInsideCode);

    Long selectOrderIdByBillNo(@Param("billNumber") String billNumber, @Param("wareInsideCode") Long wareInsideCode);

    List<OutboundDetail> selectStock(Long groupId, Long companyId, Long businessId, String billNo, Long wareInsideCode);

    Integer selectStock2(Long groupId, Long companyId, Long businessId);

=======
    List<OrderOutBoundVo>  selectStockPurchaseSaleLog(OrderQuery orderQuery);

    List<OrderOutBoundVo> selectCrmData(OrderQuery orderQuery);
>>>>>>> a586cdc80c166a39e7f1f50d4f59613873a4a4e2
}

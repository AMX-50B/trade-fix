package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.OrderSapMqVo;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.OrderQuery;
import com.lbx.tradefix.vo.query.OrderSapMqQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
public interface TradeTiDBDao {

    List<OrderOutBoundVo> selectOrderOutBound(OrderBoundQuery query);

    List<OrderSapMqVo> selectOrderSapMq(OrderSapMqQuery query);

    OrderOutBoundVo getOutboundMain(OrderBoundQuery query);

    List<OrderOutBoundVo> getOutboundDetail(@Param("outboundId") Long outboundId, @Param("wareInsideCode")Long wareInsideCode);

    List<OrderOutBoundVo> selectOrderData(OrderQuery query);

    List<OrderOutBoundVo> selectGiftData(OrderQuery query);

    List<OrderOutBoundVo> selectPromotionData(OrderQuery query);

    List<OrderOutBoundVo>  selectStockPurchaseSaleLog(OrderQuery orderQuery);
}

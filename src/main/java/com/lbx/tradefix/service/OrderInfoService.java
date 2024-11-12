package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeTiDBDao;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class OrderInfoService {
    @Autowired
    private TradeTiDBDao tiDBDao;

    @DS("tidb")
    public List<OrderOutBoundVo> getOrderData(OrderQuery query){
        return tiDBDao.selectOrderData(query);
    }
    @DS("tidb")
    public List<OrderOutBoundVo> getGiftData(OrderQuery query){
        return tiDBDao.selectGiftData(query);
    }
    @DS("tidb")
    public List<OrderOutBoundVo> getPromotionData(OrderQuery query){
        return tiDBDao.selectPromotionData(query);
    }

    @DS("tidb")
    public List<OrderOutBoundVo> getStockSaleInfo(OrderQuery orderQuery) {
        return tiDBDao.selectStockPurchaseSaleLog(orderQuery);
    }
}

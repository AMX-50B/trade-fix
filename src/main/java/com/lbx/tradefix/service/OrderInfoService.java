package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeTiDBDao;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.StockEntity;
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

<<<<<<< HEAD
    @DS("tidb")
    public List<StockEntity> getOrderData(OrderQuery query){
=======

    public List<OrderOutBoundVo> getOrderData(OrderQuery query){
>>>>>>> a586cdc80c166a39e7f1f50d4f59613873a4a4e2
        return tiDBDao.selectOrderData(query);
    }

    public List<OrderOutBoundVo> getGiftData(OrderQuery query){
        return tiDBDao.selectGiftData(query);
    }

    public List<OrderOutBoundVo> getPromotionData(OrderQuery query){
        return tiDBDao.selectPromotionData(query);
    }


    public List<OrderOutBoundVo> getStockSaleInfo(OrderQuery orderQuery) {
        return tiDBDao.selectStockPurchaseSaleLog(orderQuery);
    }

    public List<OrderOutBoundVo> getCRMData(OrderQuery orderQuery) {
        return tiDBDao.selectCrmData(orderQuery);
    }
}

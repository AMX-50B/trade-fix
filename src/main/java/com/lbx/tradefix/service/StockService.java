package com.lbx.tradefix.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeTiDBDao;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.StockEntity;
import com.lbx.tradefix.vo.query.OrderQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class StockService {
    @Autowired
    private TradeTiDBDao tiDBDao;

    @DS("tidb")
    public List<StockEntity> getOrderData(OrderQuery query){
        return tiDBDao.selectOrderData(query);
    }

    @DS("tidb")
    public StockEntity getOrderData2(OrderQuery query){
        return tiDBDao.selectOrderData2(query);
    }


    @DS("tidb")
    public List<StockEntity> selectOrderDataFromOrder(OrderQuery query){
        return tiDBDao.selectOrderDataFromOrder(query);
    }


    @DS("tidb")
    public double selectNum(OrderQuery orderQuery) {
        Double num;
        try{
            num = tiDBDao.selectNum(orderQuery);
        }catch (Exception e){
            System.out.println("orderQuery:" + JSON.toJSON(orderQuery));
            throw new RuntimeException("orderQuery select 2");
        }
        return num == null ? 0D : num;
    }

    @DS("tidb")
    public String selectBillNoByOrderId(Long orderId, Long wareInsideCode) {
        return tiDBDao.selectBillNoByOrderId(orderId, wareInsideCode);
    }

    @DS("tidb")
    public Long selectOrderIdByBillNo(String billNumber, Long wareInsideCode) {
        return tiDBDao.selectOrderIdByBillNo(billNumber, wareInsideCode);
    }
}

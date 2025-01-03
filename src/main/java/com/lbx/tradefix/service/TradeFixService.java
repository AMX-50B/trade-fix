package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeFixDao;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.StockEntity;
import com.lbx.tradefix.vo.query.FixDataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class TradeFixService {
    @Autowired
    private TradeFixDao tradeFixDao;

    @DS("fix")
    public List<FixDataVo> getTradeFixList(FixDataQuery map){
        return tradeFixDao.findByCondition(map);
    }

    @DS("fix")
    public void updateFixDataVo(FixDataVo fix) {
        tradeFixDao.updateFixDataVo(fix);
    }
    @DS("fix")
    public void saveResult(StockEntity stockEntity) {
        tradeFixDao.saveResult(stockEntity);
    }

    @DS("fix")
    public List<StockEntity> list2() {
        return tradeFixDao.list2();
    }

    @DS("fix")
    public List<StockEntity> list3() {
        return tradeFixDao.list3();
    }

    @DS("fix")
    public void updateOriginNumById(Long id, Double originNum) {
        tradeFixDao.updateOriginNumById(id, originNum);
    }

    @DS("fix")
    public void updatePrice(StockEntity stockEntity) {
        tradeFixDao.updatePrice(stockEntity.getId(), stockEntity.getPrice());
    }

    @DS("fix")
    public List<StockEntity> selectByOriginNum(Double num) {
        return tradeFixDao.selectByOriginNum();
    }

    @DS("fix")
    public void updateStatue(StockEntity stockEntity) {
        tradeFixDao.updateStatue(stockEntity.getId(), stockEntity.getStatus());
    }

    @DS("fix")
    public void updateBillType(StockEntity stockEntity) {
        tradeFixDao.updateBillType(stockEntity.getId(), stockEntity.getBillType());
    }

    @DS("fix")
    public void updateChangeStockQty(StockEntity stockEntity) {
        tradeFixDao.updateChangeStockQty(stockEntity.getId(), stockEntity.getChangeStockQty());
    }


    @DS("fix")
    public void updateOrderId(StockEntity stockEntity) {
        tradeFixDao.updateOrderId(stockEntity.getId(), stockEntity.getOrderId());
    }

    @DS("fix")
    public List<StockEntity> getFixDate() {
        return tradeFixDao.getFixDate();
    }
}

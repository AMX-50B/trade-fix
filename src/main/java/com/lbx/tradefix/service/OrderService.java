package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeTiDBDao;
import com.lbx.tradefix.vo.OutboundDetail;
import com.lbx.tradefix.vo.StockEntity;
import com.lbx.tradefix.vo.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class OrderService {
    @Autowired
    private TradeTiDBDao tiDBDao;

    @DS("tidb")
    public List<StockEntity> selectOrderDataFromOrder(OrderQuery query){
        return tiDBDao.selectOrderDataFromOrder(query);
    }

    @DS("tidbc")
    public Double selectJF(Long groupId, Long companyId, Long businessId, String convertCode, Long wareInsideCode) {
        return tiDBDao.selectJF(groupId, companyId, businessId, convertCode, wareInsideCode);
    }

    @DS("tidbc")
    public int selectJF2(Long groupId, Long companyId, Long businessId, String convertCode) {
        return tiDBDao.selectJF2(groupId, companyId, businessId, convertCode);
    }

    @DS("tidb")
    public Double selectOtherGift(Long groupId, Long companyId, Long businessId, String billNo, Long wareInsideCode) {
        return tiDBDao.selectOtherGift(groupId, companyId, businessId, billNo, wareInsideCode);
    }

    @DS("tidb")
    public int selectOtherGift2(Long groupId, Long companyId, Long businessId, String billNo) {
        return tiDBDao.selectOtherGift2(groupId, companyId, businessId, billNo);
    }
    @DS("tidb")
    public Double selectOrder(Long groupId, Long companyId, Long businessId, Long orderId, Long wareInsideCode) {
        return tiDBDao.selectOrder(groupId, companyId, businessId, orderId, wareInsideCode);
    }

    @DS("tidb")
    public int selectOrder2(Long groupId, Long companyId, Long businessId, Long orderId) {
        return tiDBDao.selectOrder2(groupId, companyId, businessId, orderId);
    }

    @DS("tidb")
    public Double selectPromotionGift(Long groupId, Long companyId, Long businessId, String billNo, Long wareInsideCode) {
        return tiDBDao.selectPromotionGift(groupId, companyId, businessId, billNo, wareInsideCode);
    }

    @DS("tidb")
    public int selectPromotionGift2(Long groupId, Long companyId, Long businessId, String billNo) {
        return tiDBDao.selectPromotionGift2(groupId, companyId, businessId, billNo);
    }

    @DS("tidbd")
    public Double selectPrice(Long businessId, Long wareInsideCode) {
        return tiDBDao.selectPrice(businessId, wareInsideCode);
    }


    @DS("tidb")
    public List<OutboundDetail> selectStock(Long groupId, Long companyId, Long businessId, String billNumber, Long wareInsideCode) {
        return tiDBDao.selectStock(groupId, companyId, businessId, billNumber, wareInsideCode);
    }

    @DS("tidb")
    public Integer selectStock2(Long groupId, Long companyId, Long businessId) {
        return tiDBDao.selectStock2(groupId, companyId, businessId);
    }
}

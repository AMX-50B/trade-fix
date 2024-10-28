package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.TradeTiDBDao;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class OrderOutBoundService {
    @Autowired
    private TradeTiDBDao tiDBDao;

    @DS("tidb")
    public List<OrderOutBoundVo> getOutbound(OrderBoundQuery query){
        return tiDBDao.selectOrderOutBound(query);
    }

    public List<OtherGift>

}

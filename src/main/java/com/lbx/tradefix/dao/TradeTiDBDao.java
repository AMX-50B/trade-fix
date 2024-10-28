package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.query.OrderBoundQuery;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
public interface TradeTiDBDao {

    List<OrderOutBoundVo> selectOrderOutBound(OrderBoundQuery query);
}

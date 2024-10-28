package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Mapper
public interface TradeFixDao {
    List<FixDataVo> findByCondition(FixDataQuery map);

    int update(FixDataVo map);
}

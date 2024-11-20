package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Mapper
public interface SapDao {

    List<SAPInfo> selectSapInfo(SAPInfoQuery query);

    List<SAPInfo> selectSapDetail(Long billNo);
}

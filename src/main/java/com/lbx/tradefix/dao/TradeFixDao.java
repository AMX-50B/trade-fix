package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Mapper
public interface TradeFixDao {
    List<FixDataVo> findByCondition(FixDataQuery map);

    int update(FixDataVo map);

    int insertSapData(@Param("list") List<SAPInfo> info);

    int insertReport(@Param("list") List<ReportVo> h);

    List<ReportVo> selectReport(ReportVo vo);

    int updateReport(ReportVo vo);
}

package com.lbx.tradefix.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.FixDataQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Mapper
@DS("fix")
public interface TradeFixDao {
    List<FixDataVo> findByCondition(FixDataQuery map);
    List<FixDataVo> findXmByCondition(FixDataQuery map);

    int update(FixDataVo map);

    int insertSapData(@Param("list") List<SAPInfo> info);

    int insertReport(@Param("list") List<ReportVo> h);

    List<ReportVo> selectReport(ReportVo vo);

    int updateReport(ReportVo vo);

    FixDataVo findByLine(Long line);

    int insertFixResultStock(@Param("list") List<FixResultStock> list);

    void insertFixResultStockLy(List<FixResultStockLy> stocks);
}

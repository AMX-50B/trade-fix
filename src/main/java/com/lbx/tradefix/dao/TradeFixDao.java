package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.ReportVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.StockEntity;
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

<<<<<<< HEAD
    void saveResult(StockEntity stockEntity);

    void updateFixDataVo(FixDataVo fix);

    List<StockEntity> list2();

    List<StockEntity> list3();

    void updateOriginNumById(@Param("id") Long id, @Param("originNum") Double originNum);

    void updatePrice(@Param("id") Long id, @Param("price") Double price);

    List<StockEntity> selectByOriginNum();

    void updateStatue(@Param("id") Long id, @Param("status") Integer status);

    void updateBillType(@Param("id") Long id, @Param("billType") String billType);

    void updateChangeStockQty(@Param("id") Long id,@Param("changeStockQty")  Double changeStockQty);

    void updateOrderId(@Param("id") Long id, @Param("orderId") String orderId);

    List<StockEntity> getFixDate();

=======
    List<ReportVo> selectReport(ReportVo vo);

    int updateReport(ReportVo vo);

    FixDataVo findByLine(Long line);
>>>>>>> a586cdc80c166a39e7f1f50d4f59613873a4a4e2
}

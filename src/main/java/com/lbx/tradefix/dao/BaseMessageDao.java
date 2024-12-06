package com.lbx.tradefix.dao;

import com.lbx.tradefix.vo.OrgInfo;
import com.lbx.tradefix.vo.WareInfoVo;
import com.lbx.tradefix.vo.query.OrgInfoQuery;
import com.lbx.tradefix.vo.query.WareInfoQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Mapper
public interface BaseMessageDao {
    List<OrgInfo> selectOrgInfo(OrgInfoQuery query);

    List<WareInfoVo> selectWareInfo(WareInfoQuery query);

    WareInfoVo selectBusinessWareInfo(WareInfoQuery query);
}

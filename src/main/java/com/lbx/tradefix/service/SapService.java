package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.SapDao;
import com.lbx.tradefix.dao.TradeFixDao;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Service
public class SapService {
    @Autowired
    private SapDao sapDao;

    @DS("sap")
    public List<SAPInfo> getSAPInfo(SAPInfoQuery query){
        return sapDao.selectSapInfo(query);
    }

}

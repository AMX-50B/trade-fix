package com.lbx.tradefix.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lbx.tradefix.dao.BaseMessageDao;
import com.lbx.tradefix.vo.OrgInfo;
import com.lbx.tradefix.vo.WareInfoVo;
import com.lbx.tradefix.vo.query.OrgInfoQuery;
import com.lbx.tradefix.vo.query.WareInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Service
public class BaseMessageService {

    private static final int size = 1000;

    @Autowired
    private BaseMessageDao baseMessageDao;

    @DS("tidb")
    public Map<String,OrgInfo> getOrgMessage(Set<String> orgCodes) {
        OrgInfoQuery query = new OrgInfoQuery();
        query.setOrgCodes(new ArrayList<>(orgCodes));
        List<OrgInfo> orgList = baseMessageDao.selectOrgInfo(query);
        Map<String,OrgInfo> orgMap ;
        if(!CollectionUtils.isEmpty(orgList)){
            orgMap =  new HashMap<>(orgList.size());
            for(OrgInfo orgInfo : orgList){
                orgMap.put(orgInfo.getOrgCode(),orgInfo);
            }
        }else {
            orgMap =  new HashMap<>(0);
        }
        return orgMap;
    }
    @DS("tidb")
    public Map<String,String> getWareMessage(Set<String> wareCodes) {
        WareInfoQuery query = new WareInfoQuery();
        query.setWareCodes(new ArrayList<>(wareCodes));
        List<WareInfoVo> infoVos = baseMessageDao.selectWareInfo(query);
        Map<String,String> wareMap ;
        if(!CollectionUtils.isEmpty(infoVos)){
            wareMap =  new HashMap<>(infoVos.size());
            for(WareInfoVo wareInfoVo : infoVos){
                wareMap.put(wareInfoVo.getWareCode(),wareInfoVo.getWareInsideCode().toString());
            }
        }else {
            wareMap =  new HashMap<>(0);
        }
        return wareMap;
    }

    @DS("tidb")
    public Map<Long,WareInfoVo> getWareMessageByInsideCode(Set<Long> wareCodes) {
        WareInfoQuery query = new WareInfoQuery();
        query.setWareInsideCodes(new ArrayList<>(wareCodes));
        List<WareInfoVo> infoVos = baseMessageDao.selectWareInfo(query);
        Map<Long,WareInfoVo> wareMap ;
        if(!CollectionUtils.isEmpty(infoVos)){
            wareMap =  new HashMap<>(infoVos.size());
            for(WareInfoVo wareInfoVo : infoVos){
                wareMap.put(wareInfoVo.getWareInsideCode(),wareInfoVo);
            }
        }else {
            wareMap =  new HashMap<>(0);
        }
        return wareMap;
    }

    @DS("tidb")
    public WareInfoVo getStoreWareMessageByInsideCode(Long wareInsideCode,Long businessId) {
        WareInfoQuery query = new WareInfoQuery();
        query.setWareInsideCode(wareInsideCode);
        query.setBusinessId(businessId);
        WareInfoVo vo = baseMessageDao.selectBusinessWareInfo(query);
        return vo;
    }
}

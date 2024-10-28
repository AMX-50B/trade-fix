package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.service.BaseMessageService;
import com.lbx.tradefix.service.OrderOutBoundService;
import com.lbx.tradefix.service.SapService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.OrderOutBoundVo;
import com.lbx.tradefix.vo.SAPInfo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTests2 {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Resource
    SapService sapService;
    @Resource
    OrderOutBoundService boundService;

    @Test
    public void contextLoads() throws ParseException {
        FixDataQuery vo = new FixDataQuery();
        vo.setBilldate("2024-07-30");
        vo.setErpNum(0);
        vo.setStatus(0);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> d = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, Long> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(d);
        for (FixDataVo fix : fixList) {
            String goodsid = wareMap.get(fix.getUdfcode());
            if(StringUtils.isEmpty(goodsid)){
                System.out.println("line :"+fix.getLine()+" 商品编码"+fix.getUdfcode()+"查不到");
                continue;
            }
            SAPInfoQuery query = new SAPInfoQuery();
            query.setGoodsid(goodsid);
            query.setDeptid(fix.getDeptid());
            query.setDateUploadStart(DateUtil.getStartTime(fix.getBilldate()));
            query.setDateUploadEnd(DateUtil.getEndTime(fix.getBilldate()));
            List<SAPInfo> info = sapService.getSAPInfo(query);
            if(CollectionUtils.isEmpty(info)){
                System.out.println("line :"+fix.getLine()+" 在SAP查不到数据");
                continue;
            }
            List<String> outboundCodes = info.stream().map(SAPInfo::getPbseqid).distinct().collect(Collectors.toList());
            OrderBoundQuery boundQuery = new OrderBoundQuery();
            boundQuery.setOutboundCodes(outboundCodes);
            boundQuery.setBusinessId(orgMap.get(fix.getDeptid()));
            List<OrderOutBoundVo> outbound = boundService.getOutbound(boundQuery);
            if(CollectionUtils.isEmpty(outbound)||outbound.size()!=info.size()){
                System.out.println("line :"+fix.getLine()+" Trade 缺数据");
            }
            Map<String, OrderOutBoundVo> boundVoMap = outbound.stream().collect(Collectors.toMap(OrderOutBoundVo::getId, Function.identity()));
            for(SAPInfo sapInfo : info){
                String pbseqid = sapInfo.getPbseqid();
                OrderOutBoundVo boundVo = boundVoMap.get(pbseqid);
                if(boundVo==null){
                    System.out.println("line :"+fix.getLine()+" Trade 缺数据:"+pbseqid);
                    continue;
                }
                boolean flag = DateUtil.isSameDate(sapInfo.getBilldate(), boundVo.getModifyTime());
                if(!flag){
                    System.out.println("line :"+fix.getLine()+" 数据上传延迟，对应数据:"+boundVo.getOrderCode()+" 单据时间："+DateUtil.formatDate(boundVo.getModifyTime()));
                }
            }
        }
    }

}

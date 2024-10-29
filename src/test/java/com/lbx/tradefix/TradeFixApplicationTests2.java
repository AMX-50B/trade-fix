package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.config.BizException;
import com.lbx.tradefix.service.*;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.FixDataQuery;
import com.lbx.tradefix.vo.query.OrderBoundQuery;
import com.lbx.tradefix.vo.query.OrderSapMqQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTests2.class);

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Resource
    SapService sapService;
    @Resource
    OrderOutBoundService boundService;
    @Resource
    OrderSapMqService mqService;
    @Autowired
    private OrderSapMqService orderSapMqService;

    @Test
    public void contextLoads() throws ParseException {
        FixDataQuery vo = new FixDataQuery();
//        vo.setBilldate("2024-07-30");
        vo.setErpNum(0d);
        vo.setStatus(0);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        Set<String> deptSet = fixList.stream().map(FixDataVo::getDeptid).collect(Collectors.toSet());
        Set<String> d = fixList.stream().map(FixDataVo::getUdfcode).collect(Collectors.toSet());
        Map<String, OrgInfo> orgMap = baseMessageService.getOrgMessage(deptSet);
        Map<String, String> wareMap = baseMessageService.getWareMessage(d);
        for (FixDataVo fix : fixList) {
            try {
                String goodsid = wareMap.get(fix.getUdfcode());
                OrgInfo business = orgMap.get(fix.getDeptid());
                if(StringUtils.isEmpty(goodsid)||business==null){
                    throw BizException.builder().msg("商品编码:{}或组织:{}查不到").detail(fix.getUdfcode(),fix.getDeptid()).build();
                }
                SAPInfoQuery query = new SAPInfoQuery();
                query.setGoodsid(goodsid);
                query.setDeptid(fix.getDeptid());
                query.setDateUploadStart(DateUtil.getStartTime(fix.getBilldate()));
                query.setDateUploadEnd(DateUtil.getEndTime(fix.getBilldate()));
                List<SAPInfo> info = sapService.getSAPInfo(query);
                if(CollectionUtils.isEmpty(info)){
                    throw BizException.builder().msg("在SAP查不到:{}").detail(JSONObject.toJSONString(query)).build();
                }
                if(info.size()!=Math.abs(fix.getSapNum())){
                    throw BizException.builder().msg("在SAP查到:{}条，报告：{}条").detail(info.size()+"",fix.getSapNum().toString()).build();
                }
                List<OrderOutBoundVo> h = handle(info, business);
                fix.setStatus(1);
                fix.setRemark(JSONObject.toJSONString(h));
                tradeFixService.update(fix);
                log.info("line:{} -> 匹配成功：{}",fix.getLine(),JSONObject.toJSONString(h));
            } catch (BizException e) {
                log.error("line:{} -> 处理失败：{}",fix.getLine(),e.getMsg());
            } catch (Exception e){
                log.error("line:{} -> 处理失败：{}",fix.getLine(),e.getMessage());
            }
        }
    }

    private void handleSAPNull(SAPInfoQuery query) throws ParseException {
    }

    private List<OrderOutBoundVo> handle(List<SAPInfo> info,OrgInfo business){
        Map<Boolean, List<SAPInfo>> map = info.stream()
                .collect(Collectors.partitioningBy(sapInfo ->StringUtils.isEmpty(sapInfo.getPosbillno())));
        List<String> miss = new ArrayList<>(info.size());
        List<OrderOutBoundVo> time = new ArrayList<>(info.size());

        List<SAPInfo> gif = map.get(true);
        if(!CollectionUtils.isEmpty(gif)){
            List<String> outboundCodes = gif.stream().map(SAPInfo::getPbseqid).distinct().collect(Collectors.toList());
            Map<String, OrderOutBoundVo> gifMap = getOutboundById(business, outboundCodes);
            check(gif,gifMap,miss,time);
        }
        List<SAPInfo> sale = map.get(false);
        if(!CollectionUtils.isEmpty(sale)){
            List<String> orderCodes = sale.stream().map(SAPInfo::getPosbillno).distinct().collect(Collectors.toList());
            Map<String, OrderOutBoundVo> saleMap = getOutboundByOrderCode(business, orderCodes);
            check(sale,saleMap,miss,time);
        }
        if(!CollectionUtils.isEmpty(miss)){
            throw BizException.builder().msg("Trade 查不到数据:{},ORG:{}").detail(JSONObject.toJSONString(miss),JSONObject.toJSONString(business)).build();
        }
        return time;
    }

    private void check(List<SAPInfo> info,Map<String, OrderOutBoundVo> boundVoMap,List<String> miss,List<OrderOutBoundVo> time){
        for(SAPInfo sapInfo : info){
            String pbseqid = sapInfo.getPbseqid();
            OrderOutBoundVo boundVo = boundVoMap.get(pbseqid);
            if(boundVo==null){
                miss.add(pbseqid);
                continue;
            }
            boolean flag = DateUtil.isSameDate(sapInfo.getBilldate(), boundVo.getModifyTime());
            if(!flag){
                time.add(boundVo);
            }
        }
    }

    private Map<String, OrderOutBoundVo> getOutboundById(OrgInfo info,List<String> codes) {
        OrderBoundQuery boundQuery = new OrderBoundQuery();
        boundQuery.setOutboundCodes(codes);
        List<OrderOutBoundVo> outbound = boundService.getOutbound(boundQuery);
        if(CollectionUtils.isEmpty(outbound)){
           return new HashMap<>(0);
        }
        return outbound.stream().collect(Collectors.toMap(OrderOutBoundVo::getId, Function.identity()));
    }

    private Map<String, OrderOutBoundVo> getOutboundByOrderCode(OrgInfo info,List<String> codes) {
        OrderBoundQuery boundQuery = new OrderBoundQuery();
        boundQuery.setBusinessId(info.getId());
        boundQuery.setCompanyId(info.getParentOrgId());
        boundQuery.setOrderCodes(codes);
        List<OrderOutBoundVo> outbound = boundService.getOutbound(boundQuery);
        if(CollectionUtils.isEmpty(outbound)){
            return new HashMap<>(0);
        }
        return outbound.stream().collect(Collectors.toMap(OrderOutBoundVo::getOrderCode, Function.identity()));
    }

}

package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.config.BizException;
import com.lbx.tradefix.config.TaskThreadPoolConfig;
import com.lbx.tradefix.service.*;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.*;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTests3 {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTests2.class);

    @Resource
    TradeFixService tradeFixService;
    @Resource
    BaseMessageService baseMessageService;
    @Resource
    SapService sapService;
    @Autowired
    private OrderOutBoundService outBoundService;
    @Autowired
    @Qualifier(TaskThreadPoolConfig.EXPORT_EXECUTOR_BEAN_NAME)
    private Executor executor;
    @Autowired
    private OrderInfoService orderInfoService;

    @Test
    public void contextLoads() throws ParseException {
        FixDataQuery vo = new FixDataQuery();
//        vo.setBilldate("2024-07-30");
//        vo.setLine(84626L);
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
                    throw BizException.builder().code(-1).msg("商品编码:{}或组织:{}查不到").detail(fix.getUdfcode(),fix.getDeptid()).build();
                }
                SAPInfoQuery query = new SAPInfoQuery();
                query.setGoodsid(goodsid);
                query.setDeptid(fix.getDeptid());
                query.setDateUploadStart(DateUtil.getStartTime(fix.getBilldate()));
                query.setDateUploadEnd(DateUtil.getEndTime(fix.getBilldate()));
                List<SAPInfo> infos = sapService.getSAPInfo(query);
                if(CollectionUtils.isEmpty(infos)){
                    throw BizException.builder().code(-2).msg("在SAP查不到:{}").detail(JSONObject.toJSONString(query)).build();
                }
                List<ReportVo> h = new ArrayList<>(infos.size());
                for(SAPInfo info:infos){
                    ReportVo handle = handle(info, business, goodsid,DateUtil.parseDate(fix.getBilldate()));
                    if(handle!=null){
                        handle.setLine(fix.getLine());
                        h.add(handle);
                    }
                }
                FixDataVo  fixDataVo = jugeReport(h);
                fixDataVo.setLine(fix.getLine());
                tradeFixService.update(fixDataVo);
                log.info("line:{} -> 比对完成：{}",fix.getLine(),JSONObject.toJSONString(fixDataVo));
            } catch (BizException e) {
                FixDataVo  fixDataVo = new FixDataVo();
                fixDataVo.setLine(fix.getLine());
                fixDataVo.setStatus(e.getCode());
                fixDataVo.setRemark(e.getMsg());
                tradeFixService.update(fixDataVo);
                log.error("line:{} -> 处理失败：{}",fix.getLine(),e.getMsg());
            } catch (Exception e){
                log.error("line:{} -> 处理失败：{}",fix.getLine(),e.getMessage());
                throw e;
            }
        }
    }

    private FixDataVo jugeReport(List<ReportVo> h) {
        FixDataVo fixDataVo = new FixDataVo();
        executor.execute(()->{
            tradeFixService.insertReport(h);
        });
        List<ReportVo> errReport = h.stream().filter(r->r.getStatus()!=0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(errReport)){
            fixDataVo.setStatus(1);
            return fixDataVo;
        }
        Map<Integer, List<ReportVo>> map = errReport.stream().collect(Collectors.groupingBy(ReportVo::getStatus));
        if(map.size()==1){
            List<ReportVo> next = map.values().iterator().next();
            fixDataVo.setStatus(next.get(0).getStatus());
            fixDataVo.setRemark(next.get(0).getMsg());
            return fixDataVo;
        }else {
            fixDataVo.setStatus(-44);
            fixDataVo.setRemark("综合情况！");
            return fixDataVo;
        }

    }

    private ReportVo handle(SAPInfo info, OrgInfo business, String goodsid, Date date){
        ReportVo report = new ReportVo();
        report.setBillNo(info.getPbseqid());
        report.setBusinessId(business.getId());
        report.setCompanyId(business.getParentOrgId());
        report.setWareInsideCode(new Long(goodsid));
        report.setPrice(info.getPrice());
        Double ot = 0d;
        Double st = info.getTotal();

        if(info.getBilltypeid()!=null&&43==info.getBilltypeid()){
            st = -st;
        }
        report.setSapNum(st);
        OrderQuery query = new OrderQuery();
        query.setCompanyId(business.getParentOrgId());
        query.setBusinessId(business.getId());
        query.setId(info.getPbseqid());
        query.setWareInsideCode(new Long(goodsid));

        List<OrderOutBoundVo> data = getData(query, info.getFgtyp());
        report.setType(query.getType());
        if(CollectionUtils.isEmpty(data)){
            List<OrderOutBoundVo> stockSaleInfo = orderInfoService.getStockSaleInfo(query);
            if(CollectionUtils.isEmpty(stockSaleInfo)){
                report.setErpNum(ot);
                report.setStatus(-11);
                report.setMsg("订单查不到数据");
                return report;
            }else {
                for(OrderOutBoundVo outbound:stockSaleInfo){
                    ot+=outbound.getWareQty();
                }
                report.setErpNum(ot);
                report.setStatus(-22);
                report.setMsg("无订单有账页信息");
                return report;
            }

        }
        for(OrderOutBoundVo outbound:data){
            ot+=outbound.getWareQty();
        }
        if(!Objects.equals(st, ot)){
            report.setErpNum(ot);
            report.setStatus(-33);
            report.setMsg("数据差异");
            return report;
        }else if(!DateUtil.isSameDate(date, data.get(0).getCreateTime())){
            report.setErpNum(ot);
            report.setStatus(2);
            report.setMsg("上传时间差异:"+DateUtil.formatDate(date)+","+DateUtil.formatDate(data.get(0).getCreateTime()));
            return report;
        }else {
            report.setErpNum(ot);
            report.setStatus(1);
            return report;
        }
    }

    private List<OrderOutBoundVo> getData(OrderQuery query,Integer ftype){
        if(ftype==null){
            query.setType(0);
            return  orderInfoService.getOrderData(query);
        }else {
            List<OrderOutBoundVo> giftData = orderInfoService.getGiftData(query);
            query.setType(1);
            if(CollectionUtils.isEmpty(giftData)&&1== ftype){
                query.setType(2);
                return orderInfoService.getPromotionData(query);
            }
            return giftData;
        }
    }






}

package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.service.*;
import com.lbx.tradefix.utils.DateUtil;
import com.lbx.tradefix.vo.*;
import com.lbx.tradefix.vo.query.OrderQuery;
import com.lbx.tradefix.vo.query.SAPInfoQuery;
import com.lbx.tradefix.vo.query.WareInfoQuery;
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

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTests4 {

    private static final Logger log = LoggerFactory.getLogger(TradeFixApplicationTests4.class);

    private static Long mainId = 4318797185863680L;

    private static Long detailId = 4318797185900544L;

    private static String  mainSqlTemplate = "INSERT INTO t_other_gifts_stock ("+
                "id, group_id, company_id, business_id, bill_no, bill_status, stock_change_status, stock_type_id,"+
                "give_date, cause_id, remark, create_user, modify_user, count_money, effective_time, order_no,"+
                "exchange_bonus, member_no, version, member_id) VALUES " +
                "({id}, {groupId}, {companyId}, {businessId}, {billNo}, {billStatus}, {stockChangeStatus}, {stockTypeId},"+
                "{giveDate}, {causeId}, {remark}, {createUser}, {modifyUser}, {countMoney}, {effectiveTime}, {orderNo},"+
                "{exchangeBonus}, {memberNo}, {version}, {memberId});";
    private static String detailSqlTemplate ="INSERT INTO t_other_gifts_stock_detail ("+
                "id, other_gifts_stock_id, group_id, ware_inside_code, made_number, batch_code, give_num,"+
                "phone, reference_sale_price, cost_amount, order_code, remark, `row_number`, business_id, bonus) VALUES ("+
                "{id}, {otherGiftsStockId}, {groupId}, {wareInsideCode}, {madeNumber}, {batchCode}, {giveNum},"+
                "{phone}, {referenceSalePrice}, {costAmount}, {orderCode}, {remark}, {rowNumber}, {businessId}, {bonus});";

    @Resource
    TradeFixService tradeFixService;
    @Autowired
    BaseMessageService baseMessageService;
    @Autowired
    SapService sapService;
    @Autowired
    OrderInfoService orderInfoService;


    @Test
    public void contextLoads() throws ParseException {
        ReportVo query = new ReportVo();
        query.setStatus(-77);
//        query.setStatus(-11);
       List<ReportVo> vos = tradeFixService.getReport(query);
        Map<Long, List<ReportVo>> map = vos.stream().collect(Collectors.groupingBy(ReportVo::getBillNo));
        List<String> content = new ArrayList<>(vos.size()*3);
       for(Long id : map.keySet()) {
           List<ReportVo> list = map.get(id);
           ReportVo vo = list.get(0);
           OrderQuery query1 = new OrderQuery();
           query1.setCompanyId(vo.getCompanyId());
           query1.setBusinessId(vo.getBusinessId());
           query1.setId(vo.getBillNo());
           List<OrderOutBoundVo> saleInfo = orderInfoService.getStockSaleInfo(query1);
           Map<Long, List<OrderOutBoundVo>> collect = saleInfo.stream().collect(Collectors.groupingBy(OrderOutBoundVo::getWareInsideCode));
           OrderOutBoundVo sapInfo = saleInfo.get(0);

           OtherGifsStockVo v = new OtherGifsStockVo();
           mainId --;
           v.setId(mainId);
           v.setGroupId(10000L);
           v.setBusinessId(vo.getBusinessId());
           v.setCompanyId(vo.getCompanyId());
           v.setBillNo(vo.getBillNo().toString());
           // 已生效
           v.setBillStatus(4);
           // 库存已扣减
           v.setStockChangeStatus(7);
           // 其它发放
           v.setStockTypeId(600127L);
           //
           v.setGiveDate(DateUtil.formatDate(sapInfo.getCreateTime(),DateUtil.YYYY_MM_DD));
           v.setRemark("sap对账数据修复");
           v.setCreateUser(1L);
           v.setModifyUser(1L);
           v.setVersion("1");

           double contMoney = 0d;
            List<String> child = new ArrayList<>();
           for(ReportVo detail : list) {
               List<OrderOutBoundVo> infos = collect.get(Long.parseLong(detail.getWareInsideCode2()));
               Long row = 1L;
               for(OrderOutBoundVo info : infos) {
                   OtherGifsStockDetailVo detailVo = new OtherGifsStockDetailVo();
                   detailId --;
                   detailVo.setId(detailId);
                   detailVo.setGroupId(10000L);
                   detailVo.setOtherGiftsStockId(mainId);
                   detailVo.setBusinessId(detail.getBusinessId());
                   detailVo.setWareInsideCode(Long.parseLong(detail.getWareInsideCode2()));
                   detailVo.setMadeNumber(info.getMadeNumber());
                   BigDecimal qy = BigDecimal.valueOf(info.getWareQty());
                   BigDecimal price = BigDecimal.valueOf(info.getPurchasePrice());
                   BigDecimal decimal = qy.multiply(price).setScale(2, RoundingMode.HALF_UP);
                   detailVo.setCostAmount(decimal.doubleValue());
                   detailVo.setRowNumber(row);
//                   detailVo.setReferenceSalePrice(info.getPrice());
                   detailVo.setGiveNum(info.getWareQty());
                   detailVo.setRemark("SAP对账数据修复");
                   contMoney += detailVo.getCostAmount();
                   String sql = replacePlaceholders(detailSqlTemplate, detailVo);
                    child.add(sql);
                   row++;
               }
           }
            v.setCountMoney(contMoney);
           String sql1 = replacePlaceholders(mainSqlTemplate, v);
           String zx = "-- "+vo.getBillNo();
           content.add(zx);
           content.add(sql1);
           content.addAll(child);
       }
       writeToFile("gifts.sql", content);
    }

    /**
     * 替换 SQL 模板中的占位符，并处理 null 值
     * @param template SQL 模板
     * @param v 参数值的 Map
     * @return 替换后的 SQL
     */
    public static String replacePlaceholders(String template, Object v) {
        Field[] fields = v.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String placeholder = "{" + field.getName() + "}";
            Object fieldValue = null; // 属性值
            try {
                fieldValue = field.get(v);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            String value;
            if (fieldValue == null) {
                value = "NULL"; // 处理 null 值
            } else if (fieldValue instanceof String) {
                value = "'" + fieldValue + "'"; // 字符串加引号
            } else {
                value = fieldValue.toString(); // 数值直接替换
            }
            template = template.replace(placeholder, value);
        }
        return template;
    }

    private void writeToFile(String fileName, List<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // 生成 SQL 并写入文件
            for (String row : content) {
                writer.write(row + "\n");
            }
        } catch (IOException e) {
            log.error("写入文件时发生错误：{}", e.getMessage());
        }
    }

}

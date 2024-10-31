package com.lbx.tradefix;


import com.alibaba.fastjson.JSONObject;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.FixDataVo;
import com.lbx.tradefix.vo.query.FixDataQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplicationTests {

    @Resource
    TradeFixService tradeFixService;

    @Test
    public void contextLoads() {
        FixDataQuery vo = new FixDataQuery();
        vo.setBilldate("2024-07-30");
        vo.setErpNum(0d);
        vo.setStatus(0);
//        vo.setDeptid("1004");
//        vo.setUdfcode(1160901085L);
        List<FixDataVo> fixList = tradeFixService.getTradeFixList(vo);
        for (FixDataVo fix : fixList) {
            FixDataQuery tv = new FixDataQuery();
            tv.setDeptid(fix.getDeptid());
            tv.setUdfcode(fix.getUdfcode());
            tv.setBilldateStart("2024-07-20");
            tv.setBilldateEnd("2024-07-30");
            List<FixDataVo> all = tradeFixService.getTradeFixList(tv);
            int count = 0;
            List<Long> lines = new ArrayList<>(all.size());
            for (FixDataVo fix2 : all) {
                count += fix2.getDiffNum();
                if(!Objects.equals(fix2.getLine(), fix.getLine())){
                    lines.add(fix2.getLine());
                }
            }
            if(count==0&&!lines.isEmpty()){
                System.out.println("line:"+fix.getLine()+" 对冲成功："+ JSONObject.toJSONString(lines));
                fix.setStatus(1);
                fix.setRemark("对冲成功："+JSONObject.toJSONString(lines));
                tradeFixService.update(fix);
                for(Long l  : lines){
                    FixDataVo lv = new FixDataVo();
                    lv.setLine(l);
                    lv.setStatus(1);
                    lv.setRemark("对冲成功："+fix.getLine());
                    tradeFixService.update(lv);
                }
            }else {
                System.out.println("line:"+fix.getLine()+" 对冲失败："+ JSONObject.toJSONString(lines));
            }
        }
    }

}

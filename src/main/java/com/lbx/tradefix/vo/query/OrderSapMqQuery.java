package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.OrderSapMqVo;
import lombok.Data;

import java.util.Date;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OrderSapMqQuery extends OrderSapMqVo {
    private String modifyTimeStart;
    private String modifyTimeEnd;
}

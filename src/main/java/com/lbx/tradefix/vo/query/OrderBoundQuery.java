package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.OrderOutBoundVo;
import lombok.Data;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OrderBoundQuery extends OrderOutBoundVo {
    private String billNo;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private int billType;
}

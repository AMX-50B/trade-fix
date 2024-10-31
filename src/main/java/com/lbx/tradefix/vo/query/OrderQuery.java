package com.lbx.tradefix.vo.query;

import lombok.Data;

/**
 * @author LiuY
 * @date 2024/10/30
 **/
@Data
public class OrderQuery {
    private Long id;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private Long wareInsideCode;
    private Integer type;
}

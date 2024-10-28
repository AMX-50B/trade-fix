package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OrderOutBoundVo {
    String id;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private Long orderId;
    private String orderCode;
    private String outboundCode;
    private Integer billType;
    private Date modifyTime;
}
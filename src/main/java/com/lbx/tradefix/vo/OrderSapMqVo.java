package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OrderSapMqVo {
    String id;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private Long billNo;
    private String billType;
    private Integer state;
    private Integer version;
    private Date createTime;
    private Date modifyTime;
    private String errorMsg;
}

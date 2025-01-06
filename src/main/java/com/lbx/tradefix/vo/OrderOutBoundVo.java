package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OrderOutBoundVo {
<<<<<<< HEAD
    private String orderId;
=======
    Long id;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private Long orderId;
    private String orderCode;
    private String outboundCode;
    private String madeNumber;
    private Double purchasePrice;
    private Integer billType;
    private Date modifyTime;
    private Date createTime;
    private Long wareInsideCode;
    private Double wareQty;
>>>>>>> a586cdc80c166a39e7f1f50d4f59613873a4a4e2
}

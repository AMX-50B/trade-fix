package com.lbx.tradefix.vo;

import lombok.Data;

@Data
public class OutboundDetail {
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private String billNumber;
    private Integer billType;
    private Integer businessType;
    private Long wareInsideCode;
    private String madeNumber;
    private Long batchCode;
    private Integer stallType;
    private Double applyQty;
    private Integer stallId;
}


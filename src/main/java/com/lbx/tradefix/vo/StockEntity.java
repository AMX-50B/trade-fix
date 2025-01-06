package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StockEntity {
    private Long id;
    private Long groupId;
    private Long companyId;
    private Long businessId;
    private String billNumber;
    private String billType;
    private Double changeStockQty;
    private Double sapNum;
    private Double originNum;
    private String orderId;
    private String wareInsideCode;
    private Date billOperateTime;
    private Integer status;
    private Double price;
    private Integer line;

    private Double erpNum;
    private Date createTime;

}

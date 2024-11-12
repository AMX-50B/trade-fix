package com.lbx.tradefix.vo;

import lombok.Data;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class ReportVo {
    private Long id;
    private Long line;
    private Long companyId;
    private Long businessId;
    private Long billNo;
    private Integer status;
    private Double sapNum;
    private Double erpNum;
    private String msg;
    private Integer type;
    private Long wareInsideCode;
    private Double price;
}

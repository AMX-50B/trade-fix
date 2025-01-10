package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author LiuY
 * @date 2024/12/9
 **/
@Data
public class FixResultStockLy {
    private Integer id;
    private String billNumber;
    private String orderId;
    private String billType;
    private Double changeStockQty;
    private Double sapNum;
    private Date billOperateTime;
    private String wareInsideCode;
    private Integer status;
    private Double originNum;
    private Long businessId;
    private Long companyId;
    private Integer line;
    private String remark;
    private Date sapDate;
    private Date stockDate;
    private String result;
    private List<Long> orderIdList;
}

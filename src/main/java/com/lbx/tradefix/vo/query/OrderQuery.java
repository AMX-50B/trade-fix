package com.lbx.tradefix.vo.query;

import lombok.Data;

import java.util.Date;

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
    private String billOperateTimeStart;
    private String billOperateTimeEnd;
    private String billType;
    private String billNumber;
    private Integer type;
    private String billNum;
}

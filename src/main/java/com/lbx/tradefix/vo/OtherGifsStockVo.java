package com.lbx.tradefix.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class OtherGifsStockVo {
    private  Long id;
    private  Long groupId;
    private  Long companyId;
    private  Long businessId;
    private  String billNo;
    private  Integer billStatus;
    private  Integer stockChangeStatus;
    private  Long stockTypeId;
    private  String giveDate;
    private  Long causeId;
    private  String remark;
    private  Long createUser;
    private  Long modifyUser;
    private  Double countMoney;
    private  String effectiveTime;
    private  String orderNo;
    private  String exchangeBonus;
    private  String memberNo;
    private  String version;
    private  String memberId;
}

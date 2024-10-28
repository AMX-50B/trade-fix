package com.lbx.tradefix.vo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class FixDataVo {
    private Long line;
    private String billdate;
    private String pdeptid;
    private String erpPdeptid;
    private String pdeptname;
    private String deptid;
    private String erpDeptid;
    private String deptname;
    private String udfcode;
    private String name;
    private String billNo;
    private String billType;
    private Integer sapNum;
    private Integer erpNum;
    private String sapStatus;
    private String erpStatus;
    private String modifyTime;
    private Integer diffNum;
    private Integer status;
    private String remark;
}

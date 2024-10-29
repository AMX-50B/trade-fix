package com.lbx.tradefix.vo;

import lombok.Data;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class OrgInfo {
    private Long id;
    private String orgName;
    private String orgCode;
    private Long parentOrgId;
}

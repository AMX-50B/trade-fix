package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.OrgInfo;
import lombok.Data;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class OrgInfoQuery extends OrgInfo {
    private List<String> orgCodes;
}

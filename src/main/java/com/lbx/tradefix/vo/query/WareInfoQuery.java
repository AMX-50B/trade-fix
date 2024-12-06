package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.OrgInfo;
import com.lbx.tradefix.vo.WareInfoVo;
import lombok.Data;

import java.util.List;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class WareInfoQuery extends WareInfoVo {
    private List<String> wareCodes;
    private List<Long> wareInsideCodes;
    private Long businessId;
    private Long wareInsideCode;
}

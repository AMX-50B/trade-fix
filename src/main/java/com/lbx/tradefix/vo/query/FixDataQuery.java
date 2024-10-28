package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.FixDataVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class FixDataQuery extends FixDataVo {
    private String billdateStart;
    private String billdateEnd;
    private Integer ltDiffNum;
}

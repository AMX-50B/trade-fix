package com.lbx.tradefix.vo.query;

import com.lbx.tradefix.vo.SAPInfo;
import lombok.Data;

/**
 * @author LiuY
 * @date 2024/10/28
 **/
@Data
public class SAPInfoQuery extends SAPInfo {
    private String dateUploadStart;
    private String dateUploadEnd;
}

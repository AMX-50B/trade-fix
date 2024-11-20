package com.lbx.tradefix.vo;

import lombok.Data;

/**
 * @author LiuY
 * @date 2024/10/25
 **/
@Data
public class WareInfoVo {
    private Long id;
    private Long wareInsideCode;
    private Long groupId;
    private String wareCode;
    private String name;
    private Double priceMarket;
    private String batchCode;
}

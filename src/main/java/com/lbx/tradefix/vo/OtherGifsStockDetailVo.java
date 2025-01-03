package com.lbx.tradefix.vo;

import lombok.Data;

/**
 * @author LiuY
 * @date 2024/11/18
 **/
@Data
public class OtherGifsStockDetailVo {

    /**
     * id
     */
    private Long id;

    /**
     * 其它赠品出库id
     */
    private Long otherGiftsStockId;

    /**
     * 集团编码
     */
    private Long groupId;

    /**
     * 商品自编码
     */
    private Long wareInsideCode;

    /**
     * 生产批号
     */
    private String madeNumber;

    /**
     * 批次号
     */
    private Long batchCode;

    /**
     * 赠送数量
     */
    private Double giveNum;

    /**
     * 顾客电话
     */
    private Long phone;

    /**
     * 估值单价
     */
    private Double referenceSalePrice;

    /**
     * 成本金额
     */
    private Double costAmount;

    /**
     * 小票号
     */
    private String orderCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 行号
     */
    private Long rowNumber;

    /**
     * 门店id
     */
    private Long businessId;

    /**
     * 兑换积分
     */
    private String bonus;
}

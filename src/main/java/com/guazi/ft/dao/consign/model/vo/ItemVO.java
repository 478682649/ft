package com.guazi.ft.dao.consign.model.vo;

import com.guazi.ft.dao.consign.model.ItemDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单项VO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ItemVO extends ItemDO {
    /**
     * 商品名称
     */
    private String goodsName;
}

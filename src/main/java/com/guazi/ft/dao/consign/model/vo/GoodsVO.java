package com.guazi.ft.dao.consign.model.vo;

import com.guazi.ft.dao.consign.model.GoodsDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品VO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsVO extends GoodsDO {

    private String categoryName;
}

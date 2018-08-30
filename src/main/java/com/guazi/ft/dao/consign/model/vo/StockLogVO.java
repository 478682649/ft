package com.guazi.ft.dao.consign.model.vo;

import com.guazi.ft.dao.consign.model.StockLogDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出入库VO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StockLogVO extends StockLogDO {

	/**
	 * 操作人姓名
	 */
	private String operatorName;
	/**
	 * 类型中文
	 */
	private String typeCH;
	/**
	 * 商品名称
	 */
	private String goodsName;
}

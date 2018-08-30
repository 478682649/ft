package com.guazi.ft.dao.consign.model.vo;

import com.guazi.ft.dao.consign.model.OrderDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单VO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderVO extends OrderDO {

	/**
	 * 订单操作人
	 */
	private String username;

	/**
	 * 订单状态中文
	 */
	private String statusCH;

	/**
	 * 订单操作人姓名
	 */
	private String operatorName;
}

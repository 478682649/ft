package com.guazi.ft.dao.consign.model.dto;

import com.guazi.ft.dao.consign.model.OrderDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OrderDTO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDTO extends OrderDO {

	/**
	 * 开始日期
	 */
	private String startDate;
	/**
	 * 结束日期
	 */
	private String endDate;

	private Integer startRow;
	private Integer pageSize;
}

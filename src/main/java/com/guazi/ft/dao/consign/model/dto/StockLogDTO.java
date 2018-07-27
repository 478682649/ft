package com.guazi.ft.dao.consign.model.dto;

import com.guazi.ft.dao.consign.model.StockLogDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库日志DTO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StockLogDTO extends StockLogDO {
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

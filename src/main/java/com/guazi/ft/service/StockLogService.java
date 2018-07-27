package com.guazi.ft.service;

import com.guazi.ft.common.Page;
import com.guazi.ft.constant.StockConstant;
import com.guazi.ft.dao.consign.GoodsMapper;
import com.guazi.ft.dao.consign.OrderMapper;
import com.guazi.ft.dao.consign.StockLogMapper;
import com.guazi.ft.dao.consign.UserMapper;
import com.guazi.ft.dao.consign.model.GoodsDO;
import com.guazi.ft.dao.consign.model.StockLogDO;
import com.guazi.ft.dao.consign.model.UserDO;
import com.guazi.ft.dao.consign.model.dto.StockLogDTO;
import com.guazi.ft.dao.consign.model.vo.OrderVO;
import com.guazi.ft.dao.consign.model.vo.StockLogVO;
import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 出入库业务
 *
 * @author shichunyang
 */
@Service
public class StockLogService {

    @Autowired
    private StockLogMapper stockLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 分页查询出入库记录
     *
     * @param stockLogDTO 分页查询条件
     * @return 本次查询记录
     */
    public List<StockLogVO> list(StockLogDTO stockLogDTO, Page page) {

        int count = stockLogMapper.countPagination(stockLogDTO);
        if (count == 0) {
            return new ArrayList<>();
        }

        page.setMaxRow(count);
        stockLogDTO.setPageSize(page.getLength());
        stockLogDTO.setStartRow(page.getRowNum());

        List<StockLogVO> stockLogs = stockLogMapper.listPagination(stockLogDTO);
        this.format(stockLogs);
        return stockLogs;
    }

    private void format(List<StockLogVO> stockLogs) {
        if (stockLogs.isEmpty()) {
            return;
        }
        stockLogs.forEach(stockLog -> {
            long operator = stockLog.getOperator();
            UserDO user = userMapper.getUserById(operator);
            if (user != null) {
                stockLog.setOperatorName(user.getUsername());
            }

            stockLog.setTypeCH(StockConstant.TYPE_MAP.get(stockLog.getType()));

            long goodsId = stockLog.getGoodsId();
            GoodsDO goodsDO = goodsMapper.getGoodsById(goodsId);
            if (goodsDO != null) {
                stockLog.setGoodsName(goodsDO.getName());
            }
        });
    }

    /**
     * 出入库操作
     *
     * @param stockLogDO 仓库对象
     * @return true:操作成功
     */
    @Transactional(value = "consignTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @DataSource
    public boolean storage(StockLogDO stockLogDO) {
        // 出入库类型
        short type = stockLogDO.getType();
        // 商品id
        long goodsId = stockLogDO.getGoodsId();
        // 出入库商品数量
        int goodsNumber = stockLogDO.getGoodsNumber();

        if (goodsNumber < 1) {
            throw new FtException(RestResult.ERROR_CODE, "仓库操作失败, 商品数量不能小于1");
        }

        GoodsDO goodsDO = goodsMapper.getGoodsById(goodsId);
        if (goodsDO == null) {
            throw new FtException(RestResult.ERROR_CODE, "商品不存在，操作仓库失败");
        }

        String defaultOrderId = "0";
        if (!stockLogDO.getOrderId().equals(defaultOrderId)) {
            OrderVO order = orderMapper.getOrderById(stockLogDO.getOrderId());
            if (order == null) {
                throw new FtException(RestResult.ERROR_CODE, "仓库操作失败, 订单不存在");
            }
        }

        if (type == StockConstant.TYPE_IN) {
            // 入库操作
            goodsMapper.updateNumber(goodsId, goodsNumber);
            stockLogDO.setTypeDetail(StockConstant.TYPE_DETAIL_IN_PERSON);
            stockLogMapper.insert(stockLogDO);
        } else if (type == StockConstant.TYPE_OUT) {
            // 出库操作
            if (goodsNumber > goodsDO.getNumber()) {
                throw new FtException(RestResult.ERROR_CODE, "出库失败, 商品库存数量不足");
            }
            goodsMapper.updateNumber(goodsId, goodsNumber * -1);
            stockLogDO.setTypeDetail(StockConstant.TYPE_DETAIL_OUT_PERSON);
            stockLogMapper.insert(stockLogDO);
        } else {
            throw new FtException(RestResult.ERROR_CODE, "未知操作仓库类型");
        }
        return true;
    }
}

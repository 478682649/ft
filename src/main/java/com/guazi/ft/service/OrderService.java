package com.guazi.ft.service;

import com.guazi.ft.common.CommonUtil;
import com.guazi.ft.common.Page;
import com.guazi.ft.constant.OrderConstant;
import com.guazi.ft.constant.StockConstant;
import com.guazi.ft.dao.consign.*;
import com.guazi.ft.dao.consign.model.*;
import com.guazi.ft.dao.consign.model.dto.OrderDTO;
import com.guazi.ft.dao.consign.model.vo.ItemVO;
import com.guazi.ft.dao.consign.model.vo.OrderVO;
import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单业务类
 *
 * @author shichunyang
 */
@Service
@Slf4j
public class OrderService {

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ItemMapper itemMapper;

	@Autowired
	private GoodsMapper goodsMapper;

	@Autowired
	private StockLogMapper stockLogMapper;

	@DataSource(DataSource.slave)
	public List<OrderVO> list(OrderDTO orderDTO, Page page) {
		int count = orderMapper.countPagination(orderDTO);
		if (count == 0) {
			return new ArrayList<>();
		}

		page.setMaxRow(count);

		orderDTO.setStartRow(page.getRowNum());
		orderDTO.setPageSize(page.getLength());

		List<OrderVO> orders = orderMapper.listPagination(orderDTO);
		orders.forEach(this::format);

		return orders;
	}

	/**
	 * 格式化订单信息
	 *
	 * @param order 订单信息
	 */
	private void format(OrderVO order) {
		// 操作人
		long operator = order.getOperator();
		UserDO userDO = userMapper.getUserById(operator);
		if (userDO != null) {
			order.setOperatorName(userDO.getUsername());
		}

		// 订单中文状态
		order.setStatusCH(OrderConstant.STATUS_MAP.get(order.getStatus()));
	}

	/**
	 * 获取订单信息
	 *
	 * @param id 订单id
	 * @return 订单信息
	 */
	@DataSource(DataSource.slave)
	public OrderVO get(String id) {
		OrderVO order = orderMapper.getOrderById(id);
		if (order == null) {
			return new OrderVO();
		}

		this.format(order);
		return order;
	}

	/**
	 * 查询订单项
	 *
	 * @param id 订单id
	 * @return 订单项
	 */
	@DataSource(DataSource.slave)
	public List<ItemVO> listItems(String id) {
		List<ItemVO> items = itemMapper.selectByOrderId(id);
		if (items == null || items.isEmpty()) {
			return new ArrayList<>();
		}

		items.forEach(this::format);

		return items;
	}

	public void format(ItemVO item) {
		GoodsDO goodsDO = goodsMapper.getGoodsById(item.getGoodsId());
		if (goodsDO == null) {
			return;
		}

		item.setGoodsName(goodsDO.getName());
	}

	/**
	 * 添加订单信息
	 *
	 * @param orderDTO 订单信息
	 * @return true:添加成功
	 */
	@DataSource
	@Transactional(value = "consignTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public boolean add(OrderDTO orderDTO) {

		OrderDO orderDO = new OrderDO();
		String orderId = CommonUtil.getOrderNumber();
		orderDO.setId(orderId);
		orderDO.setOperator(orderDTO.getOperator());
		orderDO.setStatus(OrderConstant.STATUS_READY);
		orderDO.setUsername(orderDTO.getUsername());
		orderDO.setPhone(orderDTO.getPhone());
		orderDO.setAddress(orderDTO.getAddress());
		orderDO.setTotalAmount(orderDTO.getTotalAmount());
		orderDO.setRemark(orderDTO.getRemark());
		int flag = orderMapper.insert(orderDO);

		return flag == 1;
	}

	public void checkItem(ItemDO item) {

		if (item.getGoodsNumber() != null) {
			int goodsNumber = item.getGoodsNumber();
			if (goodsNumber < 1) {
				throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 商品数量不能小于1");
			}
		}

		if (item.getGoodsId() != null) {
			long goodsId = item.getGoodsId();
			GoodsDO goodsDO = goodsMapper.getGoodsById(goodsId);
			if (goodsDO == null) {
				throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 商品不存在");
			}
		}

		String orderId = item.getOrderId();
		OrderVO order = orderMapper.getOrderById(orderId);
		if (order == null) {
			throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 订单不存在");
		}

		if (order.getStatus() != OrderConstant.STATUS_READY) {
			throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 订单已经不是待确认状态了");
		}

		if (item.getId() != null) {
			ItemDO itemDO = itemMapper.selectById(item.getId());
			if (itemDO == null) {
				throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 订单项不存在");
			}

			if (!itemDO.getOrderId().equals(orderId)) {
				throw new FtException(RestResult.ERROR_CODE, "校验订单项失败, 订单项与订单不匹配");
			}
		}
	}

	/**
	 * 修改订单信息
	 *
	 * @param orderDO 订单信息
	 * @return true:修改成功
	 */
	@DataSource
	public boolean update(OrderDO orderDO) {
		return orderMapper.update(orderDO) == 1;
	}

	/**
	 * 删除订单项目
	 *
	 * @param id 订单项id
	 * @return true:删除成功
	 */
	@DataSource
	public boolean deleteItem(long id, String orderId) {
		ItemDO item = new ItemDO();
		item.setId(id);
		item.setOrderId(orderId);
		// 核查订单项
		this.checkItem(item);
		return itemMapper.delete(id) == 1;
	}

	/**
	 * 修改订单项
	 *
	 * @param id          订单项id
	 * @param goodsNumber 商品数量
	 * @return true:修改成功
	 */
	@DataSource
	public boolean updateItem(long id, int goodsNumber, String orderId) {
		ItemDO itemDO = new ItemDO();
		itemDO.setId(id);
		itemDO.setGoodsNumber(goodsNumber);
		itemDO.setOrderId(orderId);

		// 核查订单项
		this.checkItem(itemDO);

		return itemMapper.update(itemDO) == 1;
	}

	/**
	 * 添加订单项信息
	 *
	 * @param orderId     订单id
	 * @param goodsId     商品id
	 * @param goodsNumber 商品数量
	 * @return true:添加成功
	 */
	@DataSource
	public boolean addItem(String orderId, long goodsId, int goodsNumber) {
		ItemDO item = new ItemDO();
		item.setOrderId(orderId);
		item.setGoodsId(goodsId);
		item.setGoodsNumber(goodsNumber);

		// 核查订单项
		this.checkItem(item);

		return itemMapper.insert(item) == 1;
	}

	/**
	 * 确认订单
	 *
	 * @param orderId 订单id
	 * @return 订单确认结果
	 */
	@DataSource
	@Transactional(value = "consignTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public boolean confirm(String orderId, long userId) {
		OrderVO order = orderMapper.getOrderById(orderId);
		if (order == null) {
			throw new FtException(RestResult.ERROR_CODE, "订单确认失败, 订单不存在");
		}

		if (order.getStatus() != OrderConstant.STATUS_READY) {
			throw new FtException(RestResult.ERROR_CODE, "订单确认失败, 订单已经不是待确认状态了");
		}

		// 查询订单的所有订单项
		List<ItemVO> items = itemMapper.selectByOrderId(orderId);
		if (items.isEmpty()) {
			throw new FtException(RestResult.ERROR_CODE, "订单确认失败, 还没有添加订单详情");
		}

		items.forEach(this::checkConfirmItem);

		OrderDO update = new OrderDO();
		update.setId(order.getId());
		update.setOperator(userId);
		update.setStatus(OrderConstant.STATUS_CONFIRM);
		orderMapper.update(update);

		// 对订单的商品进行出库处理
		items.forEach(item -> {
			goodsMapper.updateNumber(item.getGoodsId(), item.getGoodsNumber() * -1);

			StockLogDO stockLogDO = new StockLogDO();
			stockLogDO.setOperator(userId);
			stockLogDO.setType(StockConstant.TYPE_OUT);
			stockLogDO.setTypeDetail(StockConstant.TYPE_DETAIL_OUT_ORDER);
			stockLogDO.setGoodsId(item.getGoodsId());
			stockLogDO.setGoodsNumber(item.getGoodsNumber());
			stockLogDO.setOrderId(orderId);
			stockLogDO.setRemark("");
			stockLogMapper.insert(stockLogDO);
		});

		return true;
	}

	private void checkConfirmItem(ItemVO item) {
		long goodsId = item.getGoodsId();
		int goodsNumber = item.getGoodsNumber();
		GoodsDO goodsDO = goodsMapper.getGoodsById(goodsId);
		if (goodsDO == null) {
			throw new FtException(RestResult.ERROR_CODE, "订单确认失败, 订单项中商品不存在");
		}

		if (goodsNumber > goodsDO.getNumber()) {
			throw new FtException(RestResult.ERROR_CODE, "订单确认失败, 商品库存数量不足");
		}
	}

	/**
	 * 订单成功
	 *
	 * @param orderId 订单id
	 * @param userId  用户id
	 * @return true:订单成功
	 */
	public boolean success(String orderId, long userId) {
		OrderVO order = orderMapper.getOrderById(orderId);
		if (order == null) {
			throw new FtException(RestResult.ERROR_CODE, "确认订单success失败, 订单不存在");
		}

		if (order.getStatus() != OrderConstant.STATUS_CONFIRM) {
			throw new FtException(RestResult.ERROR_CODE, "确认订单success失败, 订单已经不是已确认状态了");
		}

		OrderDO update = new OrderDO();
		update.setId(order.getId());
		update.setOperator(userId);
		update.setStatus(OrderConstant.STATUS_SUCCESS);
		orderMapper.update(update);

		return true;
	}

	/**
	 * 订单失败
	 *
	 * @param orderId 订单id
	 * @param userId  用户id
	 * @return true:确认订单失败成功
	 */
	public boolean fail(String orderId, long userId) {

		OrderVO order = orderMapper.getOrderById(orderId);
		if (order == null) {
			throw new FtException(RestResult.ERROR_CODE, "确认订单fail失败, 订单不存在");
		}

		if (order.getStatus() != OrderConstant.STATUS_CONFIRM) {
			throw new FtException(RestResult.ERROR_CODE, "确认订单fail失败, 订单已经不是已确认状态了");
		}

		OrderDO update = new OrderDO();
		update.setId(order.getId());
		update.setOperator(userId);
		update.setStatus(OrderConstant.STATUS_FAIL);
		orderMapper.update(update);

		// 将商品退回仓库
		List<ItemVO> items = itemMapper.selectByOrderId(orderId);
		items.forEach(item -> {
			goodsMapper.updateNumber(item.getGoodsId(), item.getGoodsNumber());

			StockLogDO stockLogDO = new StockLogDO();
			stockLogDO.setOperator(userId);
			stockLogDO.setType(StockConstant.TYPE_IN);
			stockLogDO.setTypeDetail(StockConstant.TYPE_DETAIL_IN_ORDER);
			stockLogDO.setGoodsId(item.getGoodsId());
			stockLogDO.setGoodsNumber(item.getGoodsNumber());
			stockLogDO.setOrderId(orderId);
			stockLogDO.setRemark("");
			stockLogMapper.insert(stockLogDO);
		});

		return true;
	}
}

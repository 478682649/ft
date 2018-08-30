package com.guazi.ft.service;

import com.guazi.ft.common.Page;
import com.guazi.ft.dao.consign.CategoryMapper;
import com.guazi.ft.dao.consign.GoodsMapper;
import com.guazi.ft.dao.consign.model.CategoryDO;
import com.guazi.ft.dao.consign.model.GoodsDO;
import com.guazi.ft.dao.consign.model.dto.GoodsDTO;
import com.guazi.ft.dao.consign.model.vo.GoodsVO;
import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品业务
 *
 * @author shichunyang
 */
@Service
@Slf4j
public class GoodsService {

	@Autowired
	private GoodsMapper goodsMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * 根据商品id查询商品信息
	 *
	 * @param id 商品id
	 * @return 商品信息
	 */
	@DataSource(DataSource.slave)
	public GoodsDO get(long id) {
		return goodsMapper.getGoodsById(id);
	}

	/**
	 * 根据id修改商品信息
	 *
	 * @param id         主键
	 * @param name       商品名称
	 * @param categoryId 分类id
	 * @return true:修改成功
	 */
	@DataSource
	public boolean update(long id, String name, short categoryId) {

		GoodsDO goodsDO = goodsMapper.getGoodsById(id);
		if (goodsDO == null) {
			throw new FtException(RestResult.ERROR_CODE, "商品不存在,无法修改");
		}

		CategoryDO categoryDO = categoryMapper.getCategoryById(categoryId);
		if (categoryDO == null) {
			throw new FtException(RestResult.ERROR_CODE, "商品分类不存在,无法修改");
		}

		if (goodsMapper.getGoodsByName(name) != null && goodsDO.getCategory() == categoryId) {
			throw new FtException(RestResult.ERROR_CODE, "商品名称已经存在");
		}

		goodsDO = new GoodsDO();
		goodsDO.setId(id);
		goodsDO.setName(name);
		goodsDO.setCategory(categoryId);

		return goodsMapper.update(goodsDO) == 1;
	}

	/**
	 * 添加商品
	 *
	 * @param name       商品名称
	 * @param categoryId 分类id
	 * @return true:添加成功
	 */
	@DataSource
	public boolean add(String name, short categoryId) {

		CategoryDO categoryDO = categoryMapper.getCategoryById(categoryId);
		if (categoryDO == null) {
			throw new FtException(RestResult.ERROR_CODE, "商品分类不存在");
		}

		if (goodsMapper.getGoodsByName(name) != null) {
			throw new FtException(RestResult.ERROR_CODE, "商品已经存在");
		}

		GoodsDO goodsDO = new GoodsDO();
		goodsDO.setName(name);
		goodsDO.setCategory(categoryId);

		return goodsMapper.insert(goodsDO) == 1;
	}

	/**
	 * 分页查询商品信息
	 *
	 * @param categoryId 分类id
	 * @param page       分页工具类
	 * @return 商品信息
	 */
	@DataSource(DataSource.slave)
	public List<GoodsVO> list(short categoryId, Page page) {

		GoodsDTO goodsDTO = new GoodsDTO();
		if (categoryId != 0) {
			goodsDTO.setCategory(categoryId);
		}

		int count = goodsMapper.countPaging(goodsDTO);

		if (count == 0) {
			return new ArrayList<>();
		}

		page.setMaxRow(count);

		goodsDTO.setStartRow(page.getRowNum());
		goodsDTO.setPageSize(page.getLength());

		List<GoodsVO> goods = goodsMapper.listPaging(goodsDTO);
		this.format(goods);
		return goods;
	}

	public void format(List<GoodsVO> goods) {
		if (goods.isEmpty()) {
			return;
		}

		goods.forEach(goodsVO -> {
			short category = goodsVO.getCategory();
			CategoryDO categoryDO = categoryMapper.getCategoryById(category);
			if (categoryDO != null) {
				goodsVO.setCategoryName(categoryDO.getName());
			}
		});
	}
}

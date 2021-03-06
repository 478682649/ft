package com.guazi.ft.service;

import com.guazi.ft.dao.consign.CategoryMapper;
import com.guazi.ft.dao.consign.model.CategoryDO;
import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类业务
 *
 * @author shichunyang
 */
@Service
public class CategoryService {

	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * 获取所有分类信息
	 *
	 * @return 所有分类信息
	 */
	@DataSource(DataSource.slave)
	public List<CategoryDO> listAll() {
		return categoryMapper.selectAllCategories();
	}

	/**
	 * 添加分类信息
	 *
	 * @param name 分类名称
	 * @return true:添加成功
	 */
	@DataSource
	public boolean add(String name) {
		CategoryDO categoryDO = categoryMapper.getCategoryByName(name);
		if (categoryDO != null) {
			throw new FtException(RestResult.ERROR_CODE, "请不要重复添加分类信息");
		}

		categoryDO = new CategoryDO();
		categoryDO.setName(name);

		return categoryMapper.insert(categoryDO) == 1;
	}

	/**
	 * 修改分类信息
	 *
	 * @param id   分类id
	 * @param name 分类名称
	 * @return true:修改成功
	 */
	@DataSource
	public boolean update(short id, String name) {
		CategoryDO categoryDO = categoryMapper.getCategoryById(id);
		if (categoryDO == null) {
			throw new FtException(RestResult.ERROR_CODE, "分类信息不存在");
		}
		if (categoryMapper.getCategoryByName(name) != null) {
			throw new FtException(RestResult.ERROR_CODE, "分类已经存在");
		}
		categoryDO = new CategoryDO();
		categoryDO.setId(id);
		categoryDO.setName(name);
		return categoryMapper.update(categoryDO) == 1;
	}

	/**
	 * 根据主键查询分类信息
	 *
	 * @param id 分类主键
	 * @return 分类信息
	 */
	@DataSource(DataSource.slave)
	public CategoryDO get(short id) {
		return categoryMapper.getCategoryById(id);
	}
}

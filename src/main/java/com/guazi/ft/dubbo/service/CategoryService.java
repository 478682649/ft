package com.guazi.ft.dubbo.service;

import com.guazi.ft.dao.consign.model.CategoryDO;

import java.util.List;

/**
 * CategoryService(dubbo)
 *
 * @author shichunyang
 */
public interface CategoryService {

	/**
	 * 查询所有分类
	 *
	 * @return 所有分类信息
	 */
	List<CategoryDO> listAll();

	/**
	 * 根据id查找分类信息
	 *
	 * @param id 分类id
	 * @return 分类信息
	 */
	CategoryDO get(short id);
}

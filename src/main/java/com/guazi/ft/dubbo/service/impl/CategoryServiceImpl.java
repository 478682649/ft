package com.guazi.ft.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.guazi.ft.dao.consign.CategoryMapper;
import com.guazi.ft.dao.consign.model.CategoryDO;
import com.guazi.ft.dubbo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * dubbo 服务
 *
 * @author shichunyang
 */
@Service(
        application = "ft",
        registry = {"ftRegistry"},
        protocol = {"ftProtocol"},
        delay = -1,
        executes = 150,
        actives = 150,
        timeout = 30_000,
        version = "1.0.0",
        group = "A",
        owner = "scy"
)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryDO> listAll() {
        return categoryMapper.selectAllCategories();
    }

    @Override
    public CategoryDO get(short id) {
        try {
            Thread.sleep(20_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CategoryDO categoryDO = categoryMapper.getCategoryById(id);

        log.info("dubbo调用完毕==>{}", categoryDO);

        categoryDO.setName(categoryDO.getName() + "_" + System.currentTimeMillis());

        return categoryDO;
    }
}

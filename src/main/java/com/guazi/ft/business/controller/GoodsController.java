package com.guazi.ft.business.controller;

import com.guazi.ft.aop.login.LoginCheck;
import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.common.Page;
import com.guazi.ft.dao.consign.model.vo.GoodsVO;
import com.guazi.ft.rest.RestResult;
import com.guazi.ft.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品API
 *
 * @author shichunyang
 */
@RestController
@RequestMapping(RestResult.API + "/goods")
@CrossOrigin
@Slf4j
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据id查询商品信息
     *
     * @param id 商品id
     * @return 商品信息
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @LoginCheck
    public String get(@RequestParam("id") long id) {
        return JsonUtil.object2Json(RestResult.getSuccessRestResult(goodsService.get(id)));
    }

    /**
     * 修改商品信息
     *
     * @param id         主键
     * @param name       商品名称
     * @param categoryId 分类id
     * @return 修改结果
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @LoginCheck
    public String update(
            @RequestParam("id") long id,
            @RequestParam("name") String name,
            @RequestParam("category_id") short categoryId
    ) {
        return JsonUtil.object2Json(RestResult.getSuccessRestResult(goodsService.update(id, name, categoryId)));
    }

    /**
     * 添加商品信息
     *
     * @param name       商品名称
     * @param categoryId 分类id
     * @return 添加结果
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @LoginCheck
    public String add(
            @RequestParam("name") String name,
            @RequestParam("category_id") short categoryId
    ) {
        return JsonUtil.object2Json(RestResult.getSuccessRestResult(goodsService.add(name, categoryId)));
    }

    /**
     * 分页查询商品
     *
     * @param categoryId  分类id
     * @param currentPage 当前页
     * @return 查询到的数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @LoginCheck
    public String list(
            @RequestParam(value = "category_id", required = false, defaultValue = "0") short categoryId,
            @RequestParam(value = "current_page") int currentPage
    ) {
        Page page = new Page();
        // 当前页码
        page.setPageNum(currentPage);
        // 每页记录数
        page.setLength(10);

        List<GoodsVO> list = goodsService.list(categoryId, page);

        Map<String, Object> result = new HashMap<>(16);
        result.put("list", list);
        result.put("page", page);
        return JsonUtil.object2Json(RestResult.getSuccessRestResult(result));
    }

    /**
     * 根据分类查询所有商品
     *
     * @param categoryId 分类id
     * @return 该分类下所有商品
     */
    @RequestMapping(value = "/list-by-category", method = RequestMethod.POST)
    @LoginCheck
    public String listByCategory(
            @RequestParam(value = "category_id") short categoryId
    ) {
        Page page = new Page();
        // 当前页码
        page.setPageNum(1);
        // 每页记录数
        page.setLength(10_0000);

        List<GoodsVO> list = goodsService.list(categoryId, page);

        Map<String, Object> result = new HashMap<>(16);
        result.put("list", list);
        return JsonUtil.object2Json(RestResult.getSuccessRestResult(result));
    }
}

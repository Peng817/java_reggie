package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.Category;
import com.pengyan.reggie.entity.Employee;
import com.pengyan.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类信息
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加分类成功");
    }

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件查询器
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper();
        query.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,query);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.eq(category.getType()!= null,Category::getType,category.getType());
        query.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(query);
        return R.success(categoryList);
    }
}

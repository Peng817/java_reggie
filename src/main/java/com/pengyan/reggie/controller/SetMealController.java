package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.dto.DishDto;
import com.pengyan.reggie.dto.SetmealDto;
import com.pengyan.reggie.entity.*;
import com.pengyan.reggie.service.CategoryService;
import com.pengyan.reggie.service.SetMealDishService;
import com.pengyan.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 增加
     * @param dto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto dto){

        setMealService.saveWithDish(dto);

        return R.success("套餐添加成功");
    }


    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo,lambdaQueryWrapper);

        Page<SetmealDto>  setMealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,setMealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> dtoRecords = records.stream().map((item)->{
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            dto.setCategoryName(categoryName);
            return dto;
        }).collect(Collectors.toList());
        setMealDtoPage.setRecords(dtoRecords);
        return R.success(setMealDtoPage);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto dto = setMealService.getWithDishById(id);
        return R.success(dto);
    }

    /**
     * 更新套餐
     * @param dto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto dto){
        setMealService.updateWithDish(dto);
        return R.success("修改成功");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> remove(@RequestParam List<Long> ids){
        log.info("ids{}", ids);
        //dishService.removeWithDishFlavor(idsList);
        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    };

    /**
     * 切换售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statusSwitch(@PathVariable Integer status,Long[] ids){
        List<Long> idList = Arrays.stream(ids).collect(Collectors.toList());
        log.info(idList.toString());
        log.info(status.toString());
        setMealService.statusSwitchByIds(status, idList);
        return R.success("状态更改成功");
    }

    /**
     * 查询套餐清单
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<SetmealDto>> listByCategory(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId, setmeal.getCategoryId());
        //添加条件，查询状态为1(起售)的菜品
        query.eq(setmeal.getStatus()!= null,Setmeal::getStatus, 1);
        //query.orderByAsc(Setmeal::getSort).orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setMealService.list(query);
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            Long setmealId = item.getId();
            Long categoryId = item.getCategoryId();
            LambdaQueryWrapper<SetmealDish> setmealDishQuery = new LambdaQueryWrapper<>();
            setmealDishQuery.eq(SetmealDish::getSetmealId, setmealId);
            List<SetmealDish> setmealDishes = setMealDishService.list(setmealDishQuery);
            String categoryName = categoryService.getById(categoryId).getName();
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setSetmealDishes(setmealDishes);
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> getDishListById(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> dishes = setMealDishService.list(setmealDishLambdaQueryWrapper);
        return R.success(dishes);
    }

}

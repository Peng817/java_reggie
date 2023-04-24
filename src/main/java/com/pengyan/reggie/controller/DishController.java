package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.Category;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.dto.DishDto;
import com.pengyan.reggie.entity.DishFlavor;
import com.pengyan.reggie.service.CategoryService;
import com.pengyan.reggie.service.DishFlavorService;
import com.pengyan.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 存入菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithDishFlavor(dishDto);
        String categoryCacheKey = "dish_" + dishDto.getCategoryId() + "_1";
        Boolean flag = redisTemplate.hasKey(categoryCacheKey);
        if (flag != false){
            redisTemplate.delete(categoryCacheKey);
        }
        return R.success("新增菜品成功");
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
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dtoRecords = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            BeanUtils.copyProperties(item,dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dtoRecords);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id找到菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithDishFlavor(dishDto);
        String categoryCacheKey = "dish_" + dishDto.getCategoryId() + "_1";
        Boolean flag = redisTemplate.hasKey(categoryCacheKey);
        if (flag != false){
            redisTemplate.delete(categoryCacheKey);
        }
        return R.success("修改成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        dishService.removeWithDishFlavor(ids);
        return R.success("删除成功");
    }

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
        dishService.statusSwitchByIds(status, idList);
        return R.success("状态更改成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> listByCategory(Dish dish){
        String categoryCacheKey = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtoList = null;
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(categoryCacheKey);
        if(dishDtoList != null){
            //如果存在，直接返回
            return R.success(dishDtoList);
        }
        //不存在，则去数据库查找
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1(起售)的菜品
        query.eq(Dish::getStatus, 1);
        query.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(query);
        dishDtoList = dishList.stream().map((item) -> {
            Long dishId = item.getId();
            Long categoryId = item.getCategoryId();
            LambdaQueryWrapper<DishFlavor> flavorQuery = new LambdaQueryWrapper<>();
            flavorQuery.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> flavors = dishFlavorService.list(flavorQuery);
            String categoryName = categoryService.getById(categoryId).getName();
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setFlavors(flavors);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        //查询后，同步到缓存中
        redisTemplate.opsForValue().set(categoryCacheKey,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }


    /*
    public R<List<Dish>> listByCategory(Dish dish){
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1(起售)的菜品
        query.eq(Dish::getStatus, 1);
        query.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(query);
        return R.success(dishList);
    }
*/
}

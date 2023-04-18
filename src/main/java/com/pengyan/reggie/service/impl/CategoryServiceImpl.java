package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.common.CustomException;
import com.pengyan.reggie.entity.Category;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.Employee;
import com.pengyan.reggie.entity.Setmeal;
import com.pengyan.reggie.mapper.CategoryMapper;
import com.pengyan.reggie.mapper.EmployeeMapper;
import com.pengyan.reggie.service.CategoryService;
import com.pengyan.reggie.service.DishService;
import com.pengyan.reggie.service.EmployeeService;
import com.pengyan.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;

    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        LambdaQueryWrapper<Setmeal> setMealQueryWrapper = new LambdaQueryWrapper<>();
        setMealQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int setMealCount = setMealService.count(setMealQueryWrapper);

        //查询分类是否关联菜品
        if(dishCount > 0){
            throw new CustomException("当前分类还关联了菜品,无法删除");
        }

        //查询分类是否关联套餐
        if(setMealCount > 0){
            throw new CustomException("当前分类还关联了套餐,无法删除");
        }

        //正常删除分类
        super.removeById(ids);
    }
}

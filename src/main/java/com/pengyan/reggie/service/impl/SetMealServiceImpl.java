package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.common.CustomException;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.dto.SetmealDto;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.DishFlavor;
import com.pengyan.reggie.entity.Setmeal;
import com.pengyan.reggie.entity.SetmealDish;
import com.pengyan.reggie.mapper.DishMapper;
import com.pengyan.reggie.mapper.SetMealMapper;
import com.pengyan.reggie.service.DishService;
import com.pengyan.reggie.service.SetMealDishService;
import com.pengyan.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 彭琰
 */
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto dto) {
        this.save(dto);
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
           item.setSetmealId(dto.getId());
           return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getWithDishById(Long id) {
        SetmealDto  dto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, dto);

        LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<>();
        query.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setMealDishService.list(query);
        dto.setSetmealDishes(setmealDishes);
        return dto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto dto) {
        this.updateById(dto);
        Long setmealId = dto.getId();
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<>();
        query.eq(SetmealDish::getSetmealId, setmealId);
        setMealDishService.remove(query);
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(Collection ids) {
        LambdaQueryWrapper<Setmeal> checkquery = new LambdaQueryWrapper<>();
        checkquery.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(checkquery);
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<>();
        query.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(query);
    }

    @Override
    @Transactional
    public void statusSwitchByIds(Integer status, Collection ids) {
        List<Setmeal> setmealList = this.listByIds(ids);
        setmealList = setmealList.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(setmealList);
    }
}

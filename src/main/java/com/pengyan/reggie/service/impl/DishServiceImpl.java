package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.common.CustomException;
import com.pengyan.reggie.dto.DishDto;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.DishFlavor;
import com.pengyan.reggie.entity.Employee;
import com.pengyan.reggie.mapper.DishMapper;
import com.pengyan.reggie.mapper.EmployeeMapper;
import com.pengyan.reggie.service.DishFlavorService;
import com.pengyan.reggie.service.DishService;
import com.pengyan.reggie.service.EmployeeService;
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
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    @Override
    @Transactional
    public void saveWithDishFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }


    @Override
    @Transactional
    public void updateWithDishFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper<>();
        query.eq(DishFlavor::getDishId, dishId);

        //暂时看看
        List<DishFlavor> list = dishFlavorService.list(query);

        dishFlavorService.remove(query);
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeWithDishFlavor(Collection ids) {
        LambdaQueryWrapper<Dish> checkQuery = new LambdaQueryWrapper<>();
        checkQuery.in(Dish::getId,ids).eq(Dish::getStatus,1);
        int count = this.count(checkQuery);
        if(count > 0) {
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper<>();
        query.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(query);
    }

    @Override
    public void statusSwitchByIds(Integer status, Collection ids) {
        List<Dish> dishList = this.listByIds(ids);
        dishList = dishList.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(dishList);
    }
}

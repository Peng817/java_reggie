package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.Setmeal;
import com.pengyan.reggie.entity.SetmealDish;
import com.pengyan.reggie.mapper.SetMealDishMapper;
import com.pengyan.reggie.mapper.SetMealMapper;
import com.pengyan.reggie.service.SetMealDishService;
import com.pengyan.reggie.service.SetMealService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class SetMealDishServiceImpl
        extends ServiceImpl<SetMealDishMapper, SetmealDish>
        implements SetMealDishService {
}

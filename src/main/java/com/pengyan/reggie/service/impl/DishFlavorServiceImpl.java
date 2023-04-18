package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.DishFlavor;
import com.pengyan.reggie.mapper.DishFlavorMapper;
import com.pengyan.reggie.mapper.DishMapper;
import com.pengyan.reggie.service.DishFlavorService;
import com.pengyan.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}

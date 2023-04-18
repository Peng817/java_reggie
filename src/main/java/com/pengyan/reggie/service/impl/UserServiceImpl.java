package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.SetmealDish;
import com.pengyan.reggie.entity.User;
import com.pengyan.reggie.mapper.SetMealDishMapper;
import com.pengyan.reggie.mapper.UserMapper;
import com.pengyan.reggie.service.SetMealDishService;
import com.pengyan.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {
}

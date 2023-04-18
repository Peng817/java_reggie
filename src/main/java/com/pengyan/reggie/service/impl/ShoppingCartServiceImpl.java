package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.ShoppingCart;
import com.pengyan.reggie.entity.User;
import com.pengyan.reggie.mapper.ShoppingCartMapper;
import com.pengyan.reggie.mapper.UserMapper;
import com.pengyan.reggie.service.ShoppingCartService;
import com.pengyan.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class ShoppingCartServiceImpl
        extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {
}

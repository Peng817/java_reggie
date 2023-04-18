package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.OrderDetail;
import com.pengyan.reggie.entity.Orders;
import com.pengyan.reggie.mapper.OrderDetailMapper;
import com.pengyan.reggie.mapper.OrdersMapper;
import com.pengyan.reggie.service.OrderDetailService;
import com.pengyan.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class OrderDetailServiceImpl
        extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService {
}

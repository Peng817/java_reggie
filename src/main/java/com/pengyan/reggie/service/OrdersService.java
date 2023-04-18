package com.pengyan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pengyan.reggie.entity.OrderDetail;
import com.pengyan.reggie.entity.Orders;
import com.pengyan.reggie.entity.User;

import java.util.List;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单，同时将下单的菜品细节传递到orderDetail表中
     * @param orders
     */
    public void submitWithDetails(Orders orders);

    /**
     * 从订单信息去order_detail表中取出对应菜品
     * @param orders
     * @return
     */
    public List<OrderDetail> getDetailList(Orders orders);
}

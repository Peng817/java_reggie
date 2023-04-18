package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.common.BaseContext;
import com.pengyan.reggie.entity.*;
import com.pengyan.reggie.mapper.OrdersMapper;
import com.pengyan.reggie.mapper.UserMapper;
import com.pengyan.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 彭琰
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submitWithDetails(Orders orders) {
        User user = userService.getById(BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        long orderId = IdWorker.getId();

        LambdaQueryWrapper<ShoppingCart> scQuery = new LambdaQueryWrapper<>();
        scQuery.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> scList = shoppingCartService.list(scQuery);

        AtomicInteger amount= new AtomicInteger(0);

        List<OrderDetail> orderDetailList =  scList.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setNumber(String.valueOf(orderId));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setPayMethod(1);

        this.save(orders);

        orderDetailService.saveBatch(orderDetailList);

        shoppingCartService.remove(scQuery);
    }

    @Override
    public List<OrderDetail> getDetailList(Orders orders) {
        Orders orders1 = this.getById(orders.getId());
        String number = orders1.getNumber();//订单编号

        LambdaQueryWrapper<OrderDetail> odQuery = new LambdaQueryWrapper<>();
        odQuery.eq(OrderDetail::getOrderId,number);
        return orderDetailService.list(odQuery);
    }
}

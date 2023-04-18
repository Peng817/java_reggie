package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengyan.reggie.common.BaseContext;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.dto.UserOrderDto;
import com.pengyan.reggie.entity.OrderDetail;
import com.pengyan.reggie.entity.Orders;
import com.pengyan.reggie.entity.ShoppingCart;
import com.pengyan.reggie.service.OrderDetailService;
import com.pengyan.reggie.service.OrdersService;
import com.pengyan.reggie.service.ShoppingCartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submitWithDetails(orders);
        return R.success("下单成功");
    }


    /**
     * 管理端查询订单清单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,
                        @RequestParam(required = false,value = "number") String number,
                        @RequestParam(required = false,value = "beginTime")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                        @RequestParam(required = false,value = "endTime")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime)
    {
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.between(beginTime!=null && endTime!=null,Orders::getOrderTime,beginTime,endTime);
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 管理端更改订单状态
     * @param order
     * @return
     */
    @PutMapping
    public R<String> changeOrderStatus(@RequestBody Orders order){
        ordersService.updateById(order);
        return R.success("状态已修改");
    }

    /**
     * 客户端订单清单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo = new Page(page,pageSize);
        Page<UserOrderDto> uodPageInfo = new Page();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,uodPageInfo,"records");
        List<Orders> records = pageInfo.getRecords();
        List<UserOrderDto>dtoRecords = records.stream().map((item)->{
            String number = item.getNumber();//订单编号
            LambdaQueryWrapper<OrderDetail> odQuery = new LambdaQueryWrapper<>();
            odQuery.eq(OrderDetail::getOrderId,number);
            List<OrderDetail> orderDetails = orderDetailService.list(odQuery);

            AtomicInteger sumNum = new AtomicInteger(0);
            for (OrderDetail orderDetail : orderDetails) {
                sumNum.addAndGet(orderDetail.getNumber());
            }

            UserOrderDto userOrderDto = new UserOrderDto();
            BeanUtils.copyProperties(item,userOrderDto);
            userOrderDto.setOrderDetails(orderDetails);
            userOrderDto.setSumNum(sumNum.intValue());
            return userOrderDto;
        }).collect(Collectors.toList());

        uodPageInfo.setRecords(dtoRecords);
        return R.success(uodPageInfo);
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders order){

        //依据订单号取出菜品
        List<OrderDetail> detailList = ordersService.getDetailList(order);
        //依据菜品列表逐个将这些菜品以当前id放入购物车
        for (OrderDetail orderDetail : detailList) {
            ShoppingCart cart = new ShoppingCart();
            cart.setName(orderDetail.getName());
            cart.setImage(orderDetail.getImage());
            cart.setDishId(orderDetail.getDishId());
            cart.setSetmealId(orderDetail.getSetmealId());
            cart.setDishFlavor(orderDetail.getDishFlavor());
            cart.setNumber(orderDetail.getNumber());
            cart.setAmount(orderDetail.getAmount());
            cart.setUserId(BaseContext.getCurrentId());
            shoppingCartService.save(cart);
        }

        return R.success("再来一单");
    }
}

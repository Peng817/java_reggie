package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.pengyan.reggie.common.BaseContext;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.ShoppingCart;
import com.pengyan.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class shoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        String name = shoppingCart.getName();
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getName, name).eq(ShoppingCart::getUserId,userId);
        query.eq(StringUtils.isNotEmpty(shoppingCart.getDishFlavor()),ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        query.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart inShoppingCart = shoppingCartService.getOne(query);

        if(inShoppingCart == null){
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            inShoppingCart = shoppingCart;
        }else{
            inShoppingCart.setNumber(inShoppingCart.getNumber()+1);
            shoppingCartService.updateById(inShoppingCart);
        }
        return R.success(inShoppingCart);
    }

    @PostMapping("/sub")
    public R<Object> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        ShoppingCart inShoppingCart = new ShoppingCart();
        if(dishId == null){
            //减少套餐
            query.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            inShoppingCart = shoppingCartService.getOne(query);
        }else{
            //减少菜品
            query.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            inShoppingCart = shoppingCartService.getOne(query);
        }
        Integer number = inShoppingCart.getNumber();
        if(number > 1){
            inShoppingCart.setNumber(number-1);
            shoppingCartService.updateById(inShoppingCart);
            return R.success(inShoppingCart);
        }
        shoppingCartService.remove(query);

        return R.success("减少成功");
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        query.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(query);
        return R.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(query);
        return R.success("购物车已清除");
    }


}

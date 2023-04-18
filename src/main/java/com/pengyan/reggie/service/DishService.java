package com.pengyan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pengyan.reggie.dto.DishDto;
import com.pengyan.reggie.entity.Category;
import com.pengyan.reggie.entity.Dish;

import java.util.Collection;

public interface DishService extends IService<Dish> {
    /**
     * 将dishDto的部分值存储到Dish的同时将另一部分存到DishFlavor中
     * @param dishDto
     */
    public void saveWithDishFlavor(DishDto dishDto);

    /**
     * 将dishDto的部分值更新到Dish的同时将另一部分更新到DishFlavor中
     * @param dishDto
     */
    public void updateWithDishFlavor(DishDto dishDto);

    /**
     * 将Dish中属于ids的值删除的同时将DishFlavor其dish_id属于ids的值删除
     * @param ids
     */
    public void removeWithDishFlavor(Collection ids);

    /**
     * 根据ids将Dish中对应行的数据的status切换
     * @param status
     * @param ids
     */
    public void statusSwitchByIds(Integer status,Collection ids);
}

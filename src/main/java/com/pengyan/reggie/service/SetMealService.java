package com.pengyan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.dto.SetmealDto;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.Setmeal;

import java.util.Collection;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 存储套餐到setmeal中，同时将其关联的菜品存储到setmeal_dish中
     * @param dto
     */
    public void saveWithDish(SetmealDto dto);

    /**
     * 根据Id去setmeal和其关联的setmeal_dish中查找数据存储到dto中
     * @param id
     */
    public SetmealDto getWithDishById(Long id);

    /**
     * 修改setmeal表及其关联的setmeal_dish中
     * @param dto
     */
    public void updateWithDish(SetmealDto dto);

    /**
     * 删除setmeal及其关联的setmeal_dish表
     * @param ids
     */
    public void removeWithDish(Collection ids);

    /**
     * 批量切换ids
     * @param status
     * @param ids
     */
    public void statusSwitchByIds(Integer status,Collection ids);
}

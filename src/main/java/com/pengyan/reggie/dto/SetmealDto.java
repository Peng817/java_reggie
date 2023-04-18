package com.pengyan.reggie.dto;


import com.pengyan.reggie.entity.SetmealDish;
import com.pengyan.reggie.entity.Setmeal;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

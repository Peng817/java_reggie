package com.pengyan.reggie.dto;

import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;
}

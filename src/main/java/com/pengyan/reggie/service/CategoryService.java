package com.pengyan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pengyan.reggie.entity.Category;
import com.pengyan.reggie.entity.Employee;

public interface CategoryService extends IService<Category> {

    public void remove(Long ids);
}

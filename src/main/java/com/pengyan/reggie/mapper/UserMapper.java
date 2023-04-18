package com.pengyan.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengyan.reggie.entity.Dish;
import com.pengyan.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 彭琰
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

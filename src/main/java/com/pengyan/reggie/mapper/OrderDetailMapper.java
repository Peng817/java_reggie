package com.pengyan.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengyan.reggie.entity.OrderDetail;
import com.pengyan.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 彭琰
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}

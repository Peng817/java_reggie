package com.pengyan.reggie.dto;

import com.pengyan.reggie.entity.OrderDetail;
import com.pengyan.reggie.entity.Orders;
import lombok.Data;
import org.springframework.core.annotation.Order;

import java.util.List;

@Data
public class UserOrderDto extends Orders {
    private List<OrderDetail> orderDetails;
    private Integer sumNum;
}

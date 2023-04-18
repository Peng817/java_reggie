package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.Employee;
import com.pengyan.reggie.mapper.EmployeeMapper;
import com.pengyan.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class EmployeeServiceImpl
        extends ServiceImpl<EmployeeMapper,Employee>
        implements EmployeeService {
}

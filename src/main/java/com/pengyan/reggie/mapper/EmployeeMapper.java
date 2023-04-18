package com.pengyan.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengyan.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 彭琰
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

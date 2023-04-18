package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.Employee;
import com.pengyan.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;



@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String username = employee.getUsername();
        String password = employee.getPassword();
        //密码加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //log.info(emp.toString());
        //查询失败返回用户不存在结果
        if(emp == null){
            return R.error("用户名不存在,登录失败");
        }
        //比对密码，密码失败返回密码错误结果
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，登陆失败");
        }
        //确认用户是否被冻结
        if(emp.getStatus() == 0){
            return R.error("账户已禁用");
        }
        //上述通过，则用户登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        //前端设计问题，这里新增员工传入的参数是不包含密码值的，因此需要设定一个默认密码
        //设置初始密码123456，使用md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        //  记录创建/更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //记录是谁创建/更新了这个账户，调用的是当前已经登录界面用户的id，该用户已记录在本地session中
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        boolean flag = employeeService.save(employee);


        if(flag) {
            return R.success(" ");
        }else{
            return R.error(" ");
        }
    }

    /**
     * 员工分页信息查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name ={}",page,pageSize,name);

        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件查询器
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper();
        query.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        query.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, query);
        return R.success(pageInfo);
    }

    /**
     * 修改员工
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        log.info("根据ID查询员工信息");
        if (employee != null) {
            return R.success(employee);
        }else{
            return R.error("未查到该员工信息");
        }
    }
}

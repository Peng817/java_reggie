package com.pengyan.reggie.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.User;
import com.pengyan.reggie.service.UserService;
import com.pengyan.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //生成验证码
        if(!StringUtils.isEmpty(phone)){
            String validateCode = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("Validation code:{}", validateCode);

            //将验证码存到服务器的redis下，并设置5min
            redisTemplate.opsForValue().set(phone, validateCode,5, TimeUnit.MINUTES);
            //session.setAttribute(phone, validateCode);

            return R.success("手机验证码发送成功");
        }
        //将正确验证存到session中

        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session,HttpServletRequest request) {
        log.info(user.toString());
        String phone = user.get("phone").toString();
        String code = user.get("code").toString();
        String codeInSession = redisTemplate.opsForValue().get(phone);
        if(codeInSession.equals(code)){
            LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
            query.eq(User::getPhone,phone);
            User userLog = userService.getOne(query);
            if(userLog == null){
                userLog = new User();
                userLog.setPhone(phone);
                userLog.setStatus(1);
                userService.save(userLog);
            }
            //确认用户是否被冻结
            if(userLog.getStatus() == 0){
                return R.error("账户已禁用");
            }
            //上述通过，则用户登录成功
            request.getSession().setAttribute("user",userLog.getId());

            //登录成功，删除服务器下的验证码
            redisTemplate.delete(phone);

            return R.success(userLog) ;
        }
        return R.error("登录失败");
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return R.success("退出成功");
    }

}

package com.pengyan.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.pengyan.reggie.common.BaseContext;
import com.pengyan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    /**
     * 路径匹配器
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        //log.info("拦截请求:{}", request.getRequestURI());
        //定义不需要请求的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
//                "/backend/api/**",
//                "/backend/images/**",
//                "/backend/js/**",
//                "/backend/page/**",
//                "/backend/plugins/**",
//                "/backend/styles/**",
//                "/backend/favicon.ico",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean check = check(uri, urls);

        //白名单放行
        if(check) {
            filterChain.doFilter(request, response);
            return;
        }

        //后端已登录则放行
        if(request.getSession().getAttribute("employee") != null) {
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //前端已登录则放行
        if(request.getSession().getAttribute("user") != null) {
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI, String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {return true;}
        }
        return false;
    }
}

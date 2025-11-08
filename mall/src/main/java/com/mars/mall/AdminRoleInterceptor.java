package com.mars.mall;

import com.mars.mall.consts.MallConst;
import com.mars.mall.enums.RoleEnum;
import com.mars.mall.exception.UserLoginException;
import com.mars.mall.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器：仅拦截 /admin/** 路径
 * 校验当前会话用户是否已登录且为管理员角色
 */
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object obj = request.getSession().getAttribute(MallConst.CURRENT_USER);
        if (!(obj instanceof User)) {
            // 未登录或会话失效
            throw new UserLoginException();
        }
        User user = (User) obj;
        // 只有管理员(0)可继续访问
        if (!RoleEnum.ADMIN.getCode().equals(user.getRole())) {
            throw new Exception();
        }
        return true;
    }
}
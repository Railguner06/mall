package com.mars.mall.service.impl;

import com.mars.mall.dao.UserMapper;
import com.mars.mall.enums.ResponseEnum;
import com.mars.mall.enums.RoleEnum;
import com.mars.mall.pojo.User;
import com.mars.mall.service.IUserService;
import com.mars.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;


/**
 * @description: 用户模块Service层：调用用户模块Dao层接口实现注册、登录、获取用户信息、退出登录功能
 * @author: Mars
 * @create: 2021-09-26 23:13
 **/
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;//用户模块dao实例

    @Override
    public ResponseVo<User> register(User user) {

        // 校验唯一性
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0){
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0){
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }
        if (StringUtils.hasText(user.getPhone())) {
            int countByPhone = userMapper.countByPhone(user.getPhone());
            if (countByPhone > 0){
                return ResponseVo.error(ResponseEnum.PHONE_EXIST);
            }
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        // Ensure DB NOT NULL timestamp fields are populated
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        // 支持：用户名/邮箱/手机号
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            user = userMapper.selectByEmail(username);
        }
        if (user == null) {
            user = userMapper.selectByPhone(username);
        }
        if (user == null){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        user.setPassword("");
        return ResponseVo.success(user);
    }

    // 新增：完善个人信息
    @Override
    public ResponseVo<User> updateProfile(Integer uid, User update) {
        // 读取当前用户
        User current = userMapper.selectByPrimaryKey(uid);
        if (current == null) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        // 唯一性校验（仅当有变更且非空）
        if (StringUtils.hasText(update.getUsername())
                && !update.getUsername().equals(current.getUsername())) {
            if (userMapper.countByUsername(update.getUsername()) > 0) {
                return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
            }
        }
        if (StringUtils.hasText(update.getEmail())
                && !update.getEmail().equals(current.getEmail())) {
            if (userMapper.countByEmail(update.getEmail()) > 0) {
                return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
            }
        }
        if (StringUtils.hasText(update.getPhone())
                && !update.getPhone().equals(current.getPhone())) {
            if (userMapper.countByPhone(update.getPhone()) > 0) {
                return ResponseVo.error(ResponseEnum.PHONE_EXIST);
            }
        }

        // 处理密码（如有）
        if (StringUtils.hasText(update.getPassword())) {
            update.setPassword(DigestUtils.md5DigestAsHex(update.getPassword().getBytes(StandardCharsets.UTF_8)));
        }

        // 设置主键并更新（选择性）
        update.setId(uid);
        int row = userMapper.updateByPrimaryKeySelective(update);
        if (row <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        User refreshed = userMapper.selectByPrimaryKey(uid);
        if (refreshed != null) {
            refreshed.setPassword("");
        }
        return ResponseVo.success(refreshed);
    }
}

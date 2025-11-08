package com.mars.mall.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserUpdateForm {
    // 以下字段均为可选：只更新传入的字段
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误")
    private String phone;

    @Size(min = 6, message = "密码至少6位")
    private String password;
}
package com.mars.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author lsl
 * @Date 2025/11/8 19:27
 * @Version 1.0
 */
@Data
public class RefundForm {//退款表单
    @NotNull(message = "退款原因不能为空")
    private String reason; // 退款原因
}

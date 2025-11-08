package com.mars.mall.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author lsl
 * @Date 2025/11/8 18:16
 * 用户评价表单
 */
@Data
public class ReviewCreateForm {

    @NotNull
    private Long orderNo;       // 订单号

    @NotNull
    private Integer productId;  // 商品ID

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;      // 评分 (1-5 星)

    private String content;     // 评价内容

    private String images;      // 晒单图片 URL 列表 (JSON 格式)

    private Boolean isAnonymous = false; // 是否匿名
}

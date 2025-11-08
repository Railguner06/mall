package com.mars.mall.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author lsl
 * @Date 2025/11/8 18:25
 * @Version 1.0
 */
@Data
public class ReviewVo {
    private Integer id;
    private Long orderNo;
    private Integer productId;
    private Integer score;
    private String content;
    private String images;
    private Date createTime;

    // 脱敏后的用户信息
    private String username;
    private Integer userId;
}

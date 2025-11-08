package com.mars.mall.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author lsl
 * @Date 2025/11/8 18:15
 * 用户评价
 */
@Data
public class Review {
    private Integer id;
    private Integer userId;
    private Long orderNo;
    private Integer productId;
    private Integer score;         // 评分: 1-5
    private String content;        // 评价内容
    private String images;         // 晒单图片 (JSON 格式)
    private Integer isAnonymous;   // 是否匿名: 0-否, 1-是
    private Date createTime;
    private Date updateTime;
}

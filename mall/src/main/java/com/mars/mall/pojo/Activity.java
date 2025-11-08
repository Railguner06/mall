package com.mars.mall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动表
 */
@Data
public class Activity {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动状态：0-草稿，1-上线，2-下线
     */
    private Integer status;

    /**
     * 活动类型：1-满减，2-折扣，3-赠品
     */
    private Integer type;

    /**
     * 规则内容（JSON格式）
     * 例如:
     * 满减: {"threshold_amount": 200, "reduction_amount": 30, "parent_id": 100004}
     * 折扣: {"discount_rate": 0.88, "parent_id": 100001}
     * 赠品: {"parent_id": 100003, "gift_stock": 1000}
     */
    private String ruleContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

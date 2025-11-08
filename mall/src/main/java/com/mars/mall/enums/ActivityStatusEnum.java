package com.mars.mall.enums;

import lombok.Getter;


/**
 * @description: 活动状态枚举类
 * 活动状态:0-草稿，1-上线，2-下线
 * @author: Mars
 * @create: 2021-10-05 15:56
 **/
@Getter
public enum ActivityStatusEnum {

    DRAFT(0),
    ONLINE(1),
    OFFLINE(2),

    ;

    Integer code;

    ActivityStatusEnum(Integer code) {
        this.code = code;
    }
}

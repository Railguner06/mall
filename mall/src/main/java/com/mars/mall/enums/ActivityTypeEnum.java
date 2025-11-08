package com.mars.mall.enums;

import lombok.Getter;

/**
 * @description: 活动类型枚举类
 * 活动类型:1-满减，2-折扣，3-赠品
 * @author: Mars
 * @create: 2021-10-05 15:56
 **/
@Getter
public enum ActivityTypeEnum {

    FULL_REDUCTION(1),

    DISCOUNT(2),

    GIFT(3),

    ;

    Integer code;

    ActivityTypeEnum(Integer code) {
        this.code = code;
    }
}

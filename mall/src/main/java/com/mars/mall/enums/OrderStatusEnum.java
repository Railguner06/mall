package com.mars.mall.enums;

import lombok.Getter;

/**
 * @description: 订单状态枚举类
 * 订单状态:0-已取消-10-未付款，20-已付款，40-已发货，50-交易成功，60-交易关闭
 * @author: Mars
 * @create: 2021-10-05 15:56
 **/
@Getter
public enum OrderStatusEnum {

    CANCELED(0, "已取消"),

    NO_PAY(10, "未付款"),

    PAID(20, "已付款"),

    SHIPPED(40, "已发货"),

    TRADE_SUCCESS(50, "交易成功"),

    TRADE_CLOSE(60, "交易关闭"),

    REFUND_APPLY(70, "申请退款"),
    ;

    Integer code;

    String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码 (code) 获取对应的枚举实例
     */
    public static OrderStatusEnum codeOf(Integer code) {
        for (OrderStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        // 如果状态码不在枚举中，抛出运行时异常
        throw new IllegalArgumentException("Invalid OrderStatusEnum code: " + code);
    }
}

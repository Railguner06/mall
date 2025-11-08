package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.mars.mall.form.RefundForm;
import com.mars.mall.vo.OrderVo;
import com.mars.mall.vo.ResponseVo;

/**
 * @description:
 * @author: Mars
 * @create: 2021-10-05 12:00
 **/
public interface IOrderService {

    ResponseVo<OrderVo> create(Integer uid,Integer shippingId);

    ResponseVo<PageInfo> list(Integer uid,Integer pageNum,Integer pageSize);

    ResponseVo<OrderVo> detail(Integer uid,Long orderNo);

    ResponseVo cancel(Integer uid,Long orderNo);

    void paid(Long orderNo);//修改订单状态为已付款

    //后台管理发货
    ResponseVo ship(Long orderNo);
    // 用户确认收货
    ResponseVo receive(Integer uid, Long orderNo);

    // 用户申请退款
    ResponseVo applyRefund(Integer uid, Long orderNo, RefundForm form);

    // 平台批准退款（后管）
    ResponseVo approveRefund(Long orderNo);

}

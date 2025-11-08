package com.mars.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mars.mall.consts.MallConst;
import com.mars.mall.form.OrderCreateForm;
import com.mars.mall.form.RefundForm;
import com.mars.mall.pojo.User;
import com.mars.mall.service.IOrderService;
import com.mars.mall.vo.OrderVo;
import com.mars.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mars.mall.enums.OrderStatusEnum;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 订单模块Controller层
 **/
@RestController
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 创建订单
     * @param form 包含了shippingId的请求表单
     * @param session 保存了当前登录的用户信息，可以拿到uid
     * @return
     */
    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
                                      HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.create(user.getId(),form.getShippingId());
    }

    /**
     * 将指定用户的所有订单罗列成订单列表
     * @param pageNum 页码
     * @param pageSize 页容纳条目数
     * @param session 保存了当前登录的用户信息，可以拿到uid
     * @return
     */
    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.list(user.getId(),pageNum,pageSize);
    }

    /**
     * 展示订单详情
     * @param orderNo 订单号
     * @param session 保存了当前登录的用户信息，可以拿到uid
     * @return
     */
    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
                                      HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }

    /**
     * 取消订单
     * @param orderNo 订单号
     * @param session 保存了当前登录的用户信息，可以拿到uid
     * @return
     */
    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable Long orderNo,
                             HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.cancel(user.getId(),orderNo);
    }

    /**
     * 【实现功能】：用户确认收货 (PUT /orders/{orderNo}/receive)
     */
    @PutMapping("/orders/{orderNo}/receive")
    public ResponseVo receive(@PathVariable Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.receive(user.getId(), orderNo);
    }

    /**
     * 【实现功能】：后管发货 (PUT /orders/{orderNo}/ship)
     */
    @PutMapping("/orders/{orderNo}/ship")
    public ResponseVo ship(@PathVariable Long orderNo) {
        // 实际生产环境：需要添加管理员权限校验
        return orderService.ship(orderNo);
    }

    /**
     * 【实现功能】：用户申请退款 (POST /orders/{orderNo}/refunds)
     */
    @PostMapping("/orders/{orderNo}/refunds")
    public ResponseVo applyRefund(@PathVariable Long orderNo,
                                  @Valid @RequestBody RefundForm refundForm,
                                  HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.applyRefund(user.getId(), orderNo, refundForm);
    }

    /**
     * 【实现功能】：后管批准退款 (PUT /orders/{orderNo}/refunds/approve)
     */
    @PutMapping("/orders/{orderNo}/refunds/approve")
    public ResponseVo approveRefund(@PathVariable Long orderNo) {
        // 实际生产环境：需要添加管理员权限校验
        return orderService.approveRefund(orderNo);
    }

    /**
     * 【实现功能】：物流跟踪查询 (GET /orders/{orderNo}/logistics)
     * 复用 detail 接口返回的物流信息，并进行格式化。
     */
    @GetMapping("/orders/{orderNo}/logistics")
    public ResponseVo getLogisticsInfo(@PathVariable Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        // 复用 detail 方法获取包含物流字段的 OrderVo
        ResponseVo<OrderVo> responseVo = orderService.detail(user.getId(), orderNo);

        if (responseVo.getStatus() != 0) {
            return responseVo;
        }

        OrderVo orderVo = responseVo.getData();

        Map<String, Object> logistics = new HashMap<>();
        logistics.put("orderNo", orderNo);

        // 状态描述
        logistics.put("status", OrderStatusEnum.codeOf(orderVo.getStatus()).getDesc());

        // 核心物流信息
        logistics.put("trackingNumber", orderVo.getTrackingNumber());
        logistics.put("shippingCompany", orderVo.getShippingCompany());

        // 模拟实时轨迹查询（假设）
        if (orderVo.getTrackingNumber() != null && orderVo.getShippingVo() != null) {
            // 使用收货地址信息进行模拟轨迹
            logistics.put("trackingDetail", "包裹已到达[" + orderVo.getShippingVo().getReceiverCity() + "]分拣中心，准备派送。");
        } else {
            logistics.put("trackingDetail", "暂无物流信息，等待商家发货");
        }

        return ResponseVo.success(logistics);
    }
}

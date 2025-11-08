package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mars.mall.MallApplicationTests;
import com.mars.mall.dao.OrderMapper;
import com.mars.mall.enums.OrderStatusEnum;
import com.mars.mall.enums.ResponseEnum;
import com.mars.mall.form.CartAddForm;
import com.mars.mall.form.RefundForm;
import com.mars.mall.pojo.Order;
import com.mars.mall.vo.CartVo;
import com.mars.mall.vo.OrderVo;
import com.mars.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Transactional //测试类加事务，测试完就会回滚，以免数据库产生脏数据
public class IOrderServiceTest extends MallApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    @Autowired // 用于模拟订单状态流转
    private OrderMapper orderMapper;

    private Integer uid = 1;

    private Integer shippingId = 4;

    private Integer productId = 26;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void before(){
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void createTest() {
        ResponseVo<OrderVo> responseVo = create();
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    private ResponseVo<OrderVo> create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("result={}",gson.toJson(responseVo));
        return responseVo;
    }

    /**
     * 辅助方法：创建订单并模拟支付成功 (状态: PAID=20)
     */
    private Long createAndPay() {
        ResponseVo<OrderVo> responseVo = create();
        Long orderNo = responseVo.getData().getOrderNo();

        Order order = orderMapper.selectByOrderNo(orderNo);
        // 模拟支付成功
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        return orderNo;
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 4);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo<OrderVo> responseVo = orderService.detail(uid, vo.getData().getOrderNo());
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void cancel() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo responseVo = orderService.cancel(uid, vo.getData().getOrderNo());
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    // ========================= 【新增功能 1：后管发货 / 物流跟踪】 测试 =========================

    /**
     * 测试发货成功 (PAID -> SHIPPED)
     */
    @Test
    public void shipTest_Success() {
        Long orderNo = createAndPay(); // PAID(20)

        // 执行发货 (后管操作)
        ResponseVo responseVo = orderService.ship(orderNo);
        log.info("发货结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

        // 验证数据库状态和物流信息
        Order shippedOrder = orderMapper.selectByOrderNo(orderNo);
        Assert.assertEquals(OrderStatusEnum.SHIPPED.getCode(), shippedOrder.getStatus()); // 状态 SHIPPED(40)
        Assert.assertNotNull(shippedOrder.getSendTime());
        Assert.assertTrue(shippedOrder.getTrackingNumber().startsWith("SF")); // 验证运单号格式
        Assert.assertEquals("顺丰速运", shippedOrder.getShippingCompany());
    }

    /**
     * 测试发货失败 (状态不正确：尝试发货 NO_PAY 订单)
     */
    @Test
    public void shipTest_Fail_WrongStatus() {
        Long orderNo = create().getData().getOrderNo(); // NO_PAY(10)

        ResponseVo responseVo = orderService.ship(orderNo);
        log.info("发货失败结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.ORDER_STATUS_ERROR.getCode(), responseVo.getStatus());
    }


    // ========================= 【新增功能 2：用户确认收货】 测试 =========================

    /**
     * 测试确认收货成功 (SHIPPED -> TRADE_SUCCESS)
     */
    @Test
    public void receiveTest_Success() {
        Long orderNo = createAndPay(); // PAID(20)
        orderService.ship(orderNo); // 模拟发货，状态 SHIPPED(40)

        // 执行确认收货
        ResponseVo responseVo = orderService.receive(uid, orderNo);
        log.info("确认收货结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

        // 验证数据库状态
        Order receivedOrder = orderMapper.selectByOrderNo(orderNo);
        Assert.assertEquals(OrderStatusEnum.TRADE_SUCCESS.getCode(), receivedOrder.getStatus()); // 状态 TRADE_SUCCESS(50)
        Assert.assertNotNull(receivedOrder.getEndTime());
    }

    /**
     * 测试确认收货失败 (状态不正确：尝试接收 PAID 订单)
     */
    @Test
    public void receiveTest_Fail_WrongStatus() {
        Long orderNo = createAndPay(); // PAID(20)

        ResponseVo responseVo = orderService.receive(uid, orderNo);
        log.info("确认收货失败结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.ORDER_STATUS_ERROR.getCode(), responseVo.getStatus());
    }


    // ========================= 【新增功能 3：退款/退货申请与处理】 测试 =========================

    /**
     * 测试用户申请退款成功 (PAID -> REFUND_APPLY)
     */
    @Test
    public void applyRefundTest_Success() {
        Long orderNo = createAndPay(); // PAID(20)
        RefundForm form = new RefundForm();
        form.setReason("尺码不合适");

        // 执行申请退款
        ResponseVo responseVo = orderService.applyRefund(uid, orderNo, form);
        log.info("申请退款结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

        // 验证数据库状态
        Order refundedOrder = orderMapper.selectByOrderNo(orderNo);
        Assert.assertEquals(OrderStatusEnum.REFUND_APPLY.getCode(), refundedOrder.getStatus()); // 状态 REFUND_APPLY(70)
    }

    /**
     * 测试申请退款失败 (状态不正确：尝试退款 NO_PAY 订单)
     */
    @Test
    public void applyRefundTest_Fail_WrongStatus() {
        Long orderNo = create().getData().getOrderNo(); // NO_PAY(10)
        RefundForm form = new RefundForm();
        form.setReason("不需要了");

        ResponseVo responseVo = orderService.applyRefund(uid, orderNo, form);
        log.info("申请退款失败结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.ORDER_STATUS_ERROR.getCode(), responseVo.getStatus());
    }

    /**
     * 测试平台批准退款成功 (REFUND_APPLY -> TRADE_CLOSED)
     */
    @Test
    public void approveRefundTest_Success() {
        Long orderNo = createAndPay(); // PAID(20)
        RefundForm form = new RefundForm();
        form.setReason("已损坏");
        orderService.applyRefund(uid, orderNo, form); // 模拟申请退款，状态 REFUND_APPLY(70)

        // 执行批准退款 (后管操作)
        ResponseVo responseVo = orderService.approveRefund(orderNo);
        log.info("批准退款结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

        // 验证数据库状态
        Order approvedOrder = orderMapper.selectByOrderNo(orderNo);
        Assert.assertEquals(OrderStatusEnum.TRADE_CLOSE.getCode(), approvedOrder.getStatus()); // 状态 TRADE_CLOSED(60)
        Assert.assertNotNull(approvedOrder.getCloseTime());
    }

    /**
     * 测试批准退款失败 (状态不正确：尝试批准 PAID 订单)
     */
    @Test
    public void approveRefundTest_Fail_WrongStatus() {
        Long orderNo = createAndPay(); // PAID(20)

        ResponseVo responseVo = orderService.approveRefund(orderNo);
        log.info("批准退款失败结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.ORDER_STATUS_ERROR.getCode(), responseVo.getStatus());
    }
}
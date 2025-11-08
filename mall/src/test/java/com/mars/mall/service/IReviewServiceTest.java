package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mars.mall.MallApplicationTests;
import com.mars.mall.dao.OrderMapper;
import com.mars.mall.enums.ResponseEnum;
import com.mars.mall.form.CartAddForm;
import com.mars.mall.form.ReviewCreateForm;
import com.mars.mall.pojo.Order;
import com.mars.mall.vo.OrderVo;
import com.mars.mall.vo.ResponseVo;
import com.mars.mall.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author lsl
 * @Date 2025/11/8 18:28
 * @Version 1.0
 */
@Slf4j
@Transactional // 确保测试后所有数据库操作自动回滚
public class IReviewServiceTest extends MallApplicationTests {

    @Autowired
    private IReviewService reviewService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    @Autowired
    private OrderMapper orderMapper; // 用于模拟订单状态流转

    private Integer uid = 1;
    private Integer shippingId = 4;
    private Integer productId = 26;
    private Long testOrderNo; // 存储测试订单号

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 【测试前置条件】:
     * 1. 确保购物车中有商品 (ProductId=26)
     * 2. 创建一个新订单 (状态默认为 NO_PAY)
     * 3. 模拟订单状态流转到【交易成功】(TRADE_SUCCESS=50)，以便允许进行评价
     */
    @Before
    public void setup() {
        // 1. 准备购物车数据
        CartAddForm cartForm = new CartAddForm();
        cartForm.setProductId(productId);
        cartForm.setSelected(true);
        cartService.add(uid, cartForm);

        // 2. 创建订单 (状态: NO_PAY=10)
        ResponseVo<OrderVo> orderResponse = orderService.create(uid, shippingId);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), orderResponse.getStatus());
        testOrderNo = orderResponse.getData().getOrderNo();

        // 3. 模拟订单完成：更新订单状态到 TRADE_SUCCESS(50)
        Order order = orderMapper.selectByOrderNo(testOrderNo);
        // TRADE_SUCCESS 的 code 是 50 (根据 OrderStatusEnum.java)
        order.setStatus(50);
        int row = orderMapper.updateByPrimaryKeySelective(order);
        Assert.assertTrue(row > 0);
    }

    // 辅助方法：构造一个有效的评价表单
    private ReviewCreateForm buildValidReviewForm() {
        ReviewCreateForm form = new ReviewCreateForm();
        form.setOrderNo(testOrderNo);
        form.setProductId(productId);
        form.setScore(5);
        form.setContent("商品质量非常好，物流速度很快！");
        form.setImages("[\"img_url_1.jpg\", \"img_url_2.jpg\"]");
        form.setIsAnonymous(false);
        return form;
    }

    /**
     * 【测试功能 1: 提交评价和晒单】
     */
    @Test
    public void createTest_Success() {
        ResponseVo responseVo = reviewService.create(uid, buildValidReviewForm());
        log.info("创建评价结果: {}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    /**
     * 【测试功能 2: 重复评价失败】
     */
    @Test
    public void createTest_DuplicateFail() {
        // 第一次：成功创建评价
        ResponseVo successVo = reviewService.create(uid, buildValidReviewForm());
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), successVo.getStatus());

        // 第二次：尝试重复创建评价
        ResponseVo failVo = reviewService.create(uid, buildValidReviewForm());
        log.info("重复评价结果: {}", gson.toJson(failVo));
        Assert.assertEquals(ResponseEnum.ERROR.getCode(), failVo.getStatus());
        Assert.assertTrue(failVo.getMsg().contains("已评价"));
    }

    /**
     * 【测试功能 3: 查看评价列表 (作为购买参考)】
     */
    @Test
    public void listTest_Success() {
        // 前置：先创建一个评价
        reviewService.create(uid, buildValidReviewForm());

        // 查询该商品的评价列表
        ResponseVo<PageInfo> responseVo = reviewService.list(productId, 1, 10);
        log.info("评价列表结果: {}", gson.toJson(responseVo));

        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        Assert.assertTrue(responseVo.getData().getTotal() >= 1);
        Assert.assertEquals(productId, ((ReviewVo)responseVo.getData().getList().get(0)).getProductId());
    }
}
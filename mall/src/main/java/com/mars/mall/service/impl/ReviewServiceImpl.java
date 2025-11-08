package com.mars.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mars.mall.dao.OrderItemMapper;
import com.mars.mall.dao.OrderMapper;
import com.mars.mall.dao.ReviewMapper;
import com.mars.mall.enums.OrderStatusEnum;
import com.mars.mall.enums.ResponseEnum;
import com.mars.mall.form.ReviewCreateForm;
import com.mars.mall.pojo.Order;
import com.mars.mall.pojo.Review;
import com.mars.mall.service.IReviewService;
import com.mars.mall.vo.ResponseVo;
import com.mars.mall.vo.ReviewVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author lsl
 * @Date 2025/11/8 18:22
 * @Version 1.0
 */
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper; // 辅助校验商品是否在订单中

    /**
     * 【功能：用户提交评价和晒单】
     */
    @Override
    public ResponseVo create(Integer userId, ReviewCreateForm form) {
        // 1. 校验订单和商品是否满足评价条件：
        //    a. 订单必须存在且属于当前用户。
        Order order = orderMapper.selectByOrderNo(form.getOrderNo());
        if (order == null || !order.getUserId().equals(userId)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }

        //    b. 订单状态必须是“交易成功”或“交易关闭”（即用户已收到商品）。
        if (!order.getStatus().equals(OrderStatusEnum.TRADE_SUCCESS.getCode()) &&
                !order.getStatus().equals(OrderStatusEnum.TRADE_CLOSE.getCode())) {
            return ResponseVo.error(ResponseEnum.ERROR, "订单状态不正确，无法评价");
        }

        //    c. 校验该商品是否属于该订单（简化：仅检查订单是否已评价）
        Review existingReview = reviewMapper.selectByOrderNoAndProductId(form.getOrderNo(), form.getProductId());
        if (existingReview != null) {
            return ResponseVo.error(ResponseEnum.ERROR, "该商品已评价");
        }

        // 2. 构建 Review 对象并保存
        Review review = new Review();
        BeanUtils.copyProperties(form, review);
        review.setUserId(userId);
        review.setIsAnonymous(form.getIsAnonymous() ? 1 : 0);

        int row = reviewMapper.insertSelective(review);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "评价写入失败");
        }

        // TODO: 考虑更新商品平均评分的逻辑

        return ResponseVo.success();
    }

    /**
     * 【功能：其他用户查看评价信息】
     */
    @Override
    public ResponseVo<PageInfo> list(Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 1. 从数据库获取评价列表
        List<Review> reviewList = reviewMapper.selectByProductId(productId);

        // 2. 转换为展示用的 ReviewVo (脱敏和封装)
        List<ReviewVo> reviewVoList = reviewList.stream().map(e -> {
            ReviewVo reviewVo = new ReviewVo();
            BeanUtils.copyProperties(e, reviewVo);

            // 3. 匿名处理 (脱敏)
            if (e.getIsAnonymous() == 1) {
                reviewVo.setUsername("匿名用户"); // 可以显示脱敏后的用户名
            }

            // TODO: 根据 userId 查询用户名并设置

            return reviewVo;
        }).collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(reviewList);
        pageInfo.setList(reviewVoList);

        return ResponseVo.success(pageInfo);
    }
}

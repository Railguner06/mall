package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.mars.mall.form.ReviewCreateForm;
import com.mars.mall.vo.ResponseVo;

/**
 * @Author lsl
 * @Date 2025/11/8 18:20
 * 用户评价Service层接口
 */
public interface IReviewService {
    // 创建/提交评价
    ResponseVo create(Integer userId, ReviewCreateForm form);

    // 查看某个商品的评价列表
    ResponseVo<PageInfo> list(Integer productId, Integer pageNum, Integer pageSize);
}

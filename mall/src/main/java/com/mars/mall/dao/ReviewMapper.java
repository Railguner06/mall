package com.mars.mall.dao;

import com.mars.mall.pojo.Review;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Author lsl
 * @Date 2025/11/8 18:18
 */
public interface ReviewMapper {
    // 插入一条新的评价
    int insertSelective(Review record);

    // 根据商品ID查询评价列表（供其他用户查看和参考）
    List<Review> selectByProductId(@Param("productId") Integer productId);

    // 校验用户是否已对某订单中的某商品评价过
    Review selectByOrderNoAndProductId(@Param("orderNo") Long orderNo, @Param("productId") Integer productId);
}

package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.mars.mall.vo.ProductDetailVo;
import com.mars.mall.vo.ResponseVo;

/**
 * @description:
 * @author: Mars
 * @create: 2021-10-02 00:42
 **/
public interface IProductService {

    // 兼容旧签名（分类分页）
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    // 新增签名（分类 + 关键字搜索 + 分页）
    ResponseVo<PageInfo> list(Integer categoryId, String keyword, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}

package com.mars.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mars.mall.consts.MallConst;
import com.mars.mall.form.ReviewCreateForm;
import com.mars.mall.pojo.User;
import com.mars.mall.service.IReviewService;
import com.mars.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @Author lsl
 * @Date 2025/11/8 18:24
 * @Version 1.0
 */
@RestController
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    /**
     * 【功能：用户提交评价和晒单】
     * POST /reviews
     * 需要登录 (Interceptor 已处理)
     */
    @PostMapping("/reviews")
    public ResponseVo create(@Valid @RequestBody ReviewCreateForm form,
                             HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return reviewService.create(user.getId(), form);
    }

    /**
     * 【功能：查看评价信息，作为购买参考】
     * GET /reviews/{productId}
     * 不需要登录
     */
    @GetMapping("/reviews/{productId}")
    public ResponseVo<PageInfo> list(@PathVariable Integer productId,
                                     @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return reviewService.list(productId, pageNum, pageSize);
    }
}

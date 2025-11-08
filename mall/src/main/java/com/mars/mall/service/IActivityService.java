package com.mars.mall.service;

import com.github.pagehelper.PageInfo;
import com.mars.mall.pojo.Activity;
import com.mars.mall.vo.ResponseVo;

/**
 * 活动模块 service 接口
 */
public interface IActivityService {

    ResponseVo<Activity> create(Activity activity);

    ResponseVo<Activity> update(Integer id, Activity activity);

    ResponseVo<Boolean> changeStatus(Integer id, Integer status);

    ResponseVo<Activity> detail(Integer id);

    ResponseVo<PageInfo<Activity>> list(Integer status, Integer type, String nameLike,
                                        Integer pageNum, Integer pageSize);
}
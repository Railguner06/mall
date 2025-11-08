package com.mars.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mars.mall.enums.RequireRole;
import com.mars.mall.enums.RoleEnum;
import com.mars.mall.pojo.Activity;
import com.mars.mall.service.IActivityService;
import com.mars.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 活动模块 Controller
 * 管理员接口统一放在 /admin/activities 路径下
 */
@RestController
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    /**
     * 创建活动（管理员）
     */
    @PostMapping("/admin/activities")
    @RequireRole(RoleEnum.ADMIN)
    public ResponseVo<Activity> create(@RequestBody Activity activity) {
        return activityService.create(activity);
    }

    /**
     * 更新活动（管理员）
     */
    @PutMapping("/admin/activities/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public ResponseVo<Activity> update(@PathVariable Integer id,
                                       @RequestBody Activity activity) {
        return activityService.update(id, activity);
    }

    /**
     * 修改活动状态（管理员）
     */
    @PutMapping("/admin/activities/{id}/status")
    @RequireRole(RoleEnum.ADMIN)
    public ResponseVo<Boolean> changeStatus(@PathVariable Integer id,
                                            @RequestParam Integer status) {
        return activityService.changeStatus(id, status);
    }

    /**
     * 活动详情（管理员）
     */
    @GetMapping("/admin/activities/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public ResponseVo<Activity> detail(@PathVariable Integer id) {
        return activityService.detail(id);
    }

    /**
     * 活动列表（管理员，支持条件+分页）
     */
    @GetMapping("/admin/activities")
    @RequireRole(RoleEnum.ADMIN)
    public ResponseVo<PageInfo<Activity>> list(@RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Integer type,
                                               @RequestParam(required = false) String nameLike,
                                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return activityService.list(status, type, nameLike, pageNum, pageSize);
    }
}
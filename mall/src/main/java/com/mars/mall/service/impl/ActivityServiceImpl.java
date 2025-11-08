package com.mars.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mars.mall.dao.ActivityMapper;
import com.mars.mall.enums.ResponseEnum;
import com.mars.mall.pojo.Activity;
import com.mars.mall.service.IActivityService;
import com.mars.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements IActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public ResponseVo<Activity> create(Activity activity) {
        if (!validActivityForCreate(activity)) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR);
        }
        int rows = activityMapper.insertSelective(activity);
        if (rows <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "创建活动失败");
        }
        return ResponseVo.success(activity);
    }

    @Override
    public ResponseVo<Activity> update(Integer id, Activity activity) {
        Activity exist = activityMapper.selectByPrimaryKey(id);
        if (exist == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "活动不存在");
        }
        // 基本校验：时间窗口/类型/状态
        if (!validActivityForUpdate(activity)) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR);
        }
        activity.setId(id);
        int rows = activityMapper.updateByPrimaryKeySelective(activity);
        if (rows <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "更新活动失败");
        }
        Activity latest = activityMapper.selectByPrimaryKey(id);
        return ResponseVo.success(latest);
    }

    @Override
    public ResponseVo<Boolean> changeStatus(Integer id, Integer status) {
        Activity exist = activityMapper.selectByPrimaryKey(id);
        if (exist == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "活动不存在");
        }
        if (status == null || (status != 0 && status != 1 && status != 2)) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "状态非法");
        }
        Activity toUpdate = new Activity();
        toUpdate.setId(id);
        toUpdate.setStatus(status);
        int rows = activityMapper.updateByPrimaryKeySelective(toUpdate);
        if (rows <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "更新状态失败");
        }
        return ResponseVo.success(Boolean.TRUE);
    }

    @Override
    public ResponseVo<Activity> detail(Integer id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        if (activity == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "活动不存在");
        }
        return ResponseVo.success(activity);
    }

    @Override
    public ResponseVo<PageInfo<Activity>> list(Integer status, Integer type, String nameLike,
                                               Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        String likeParam = null;
        if (nameLike != null && !nameLike.isEmpty()) {
            likeParam = "%" + nameLike + "%";
        }
        List<Activity> list = activityMapper.selectByFilters(status, type, likeParam);
        PageInfo<Activity> pageInfo = new PageInfo<>(list);
        return ResponseVo.success(pageInfo);
    }

    private boolean validActivityForCreate(Activity a) {
        if (a == null) return false;
        if (a.getName() == null || a.getName().isEmpty()) return false;
        if (a.getType() == null || (a.getType() != 1 && a.getType() != 2 && a.getType() != 3)) return false;
        if (a.getStatus() == null || (a.getStatus() != 0 && a.getStatus() != 1 && a.getStatus() != 2)) return false;
        if (a.getStartTime() != null && a.getEndTime() != null && a.getStartTime().isAfter(a.getEndTime())) return false;
        if (a.getEndTime() != null && a.getEndTime().isBefore(LocalDateTime.now())) return false;
        return true;
    }

    private boolean validActivityForUpdate(Activity a) {
        if (a == null) return false;
        if (a.getType() != null && (a.getType() != 1 && a.getType() != 2 && a.getType() != 3)) return false;
        if (a.getStatus() != null && (a.getStatus() != 0 && a.getStatus() != 1 && a.getStatus() != 2)) return false;
        if (a.getStartTime() != null && a.getEndTime() != null && a.getStartTime().isAfter(a.getEndTime())) return false;
        return true;
    }
}
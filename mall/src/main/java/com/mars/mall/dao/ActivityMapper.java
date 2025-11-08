package com.mars.mall.dao;

import com.mars.mall.pojo.Activity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动模块 dao 层
 */
public interface ActivityMapper {

    int insertSelective(Activity record);

    int updateByPrimaryKeySelective(Activity record);

    Activity selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    List<Activity> selectByFilters(@Param("status") Integer status,
                                   @Param("type") Integer type,
                                   @Param("nameLike") String nameLike);
}
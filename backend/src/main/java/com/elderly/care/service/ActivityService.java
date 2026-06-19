package com.elderly.care.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
public interface ActivityService extends IService<Activity> {
    Result<?> attend(Long activityId, Long seniorId);
}

package com.elderly.care.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.care.entity.Activity;

public interface ActivityService extends IService<Activity> {
    int attend(Long activityId, Long seniorId);
    boolean hasAttended(Long activityId, Long seniorId);
}

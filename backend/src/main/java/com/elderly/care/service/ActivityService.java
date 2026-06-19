package com.elderly.care.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.care.entity.Activity;
import java.util.List;

public interface ActivityService extends IService<Activity> {
    int attendActivity(Long activityId, Long seniorId);
    List<Long> getAttendedActivityIds(Long seniorId);
}

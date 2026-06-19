package com.elderly.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.entity.Activity;
import com.elderly.care.entity.ActivityRecord;
import com.elderly.care.entity.User;
import com.elderly.care.mapper.ActivityMapper;
import com.elderly.care.mapper.ActivityRecordMapper;
import com.elderly.care.mapper.UserMapper;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityRecordMapper activityRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean hasAttended(Long activityId, Long seniorId) {
        LambdaQueryWrapper<ActivityRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRecord::getActivityId, activityId)
               .eq(ActivityRecord::getSeniorId, seniorId);
        return activityRecordMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int attend(Long activityId, Long seniorId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            return -1;
        }

        if (hasAttended(activityId, seniorId)) {
            return -2;
        }

        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setSeniorId(seniorId);
        record.setStatus("signed");
        record.setSignInTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        activityRecordMapper.insert(record);

        if (activity.getRewardPoints() != null && activity.getRewardPoints() > 0) {
            User user = userMapper.selectById(seniorId);
            if (user != null) {
                user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + activity.getRewardPoints());
                userMapper.updateById(user);
            }
        }

        return activity.getRewardPoints() != null ? activity.getRewardPoints() : 0;
    }
}

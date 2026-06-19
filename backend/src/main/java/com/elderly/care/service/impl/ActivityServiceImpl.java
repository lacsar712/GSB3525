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
    @Transactional(rollbackFor = Exception.class)
    public Integer attend(Long activityId, Long seniorId) {
        if (activityId == null || seniorId == null) {
            throw new IllegalArgumentException("活动ID与老人ID不能为空");
        }

        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }

        // 防重复签到：基于 ActivityRecordMapper 查询是否已存在该老人对该活动的记录
        Long existCount = activityRecordMapper.selectCount(
                new LambdaQueryWrapper<ActivityRecord>()
                        .eq(ActivityRecord::getActivityId, activityId)
                        .eq(ActivityRecord::getSeniorId, seniorId)
        );
        if (existCount != null && existCount > 0) {
            throw new IllegalStateException("您已签到过该活动，请勿重复签到");
        }

        // 写入签到记录
        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setSeniorId(seniorId);
        record.setStatus("SIGNED");
        LocalDateTime now = LocalDateTime.now();
        record.setSignInTime(now);
        record.setCreateTime(now);
        activityRecordMapper.insert(record);

        // 发放积分（仅在首次签到时发放一次）
        Integer reward = activity.getRewardPoints();
        if (reward != null && reward > 0) {
            User user = userMapper.selectById(seniorId);
            if (user != null) {
                int current = user.getPoints() == null ? 0 : user.getPoints();
                user.setPoints(current + reward);
                userMapper.updateById(user);
            }
            return reward;
        }
        return 0;
    }
}

package com.elderly.care.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.common.Result;
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
    public Result<?> attend(Long activityId, Long seniorId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        LambdaQueryWrapper<ActivityRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRecord::getActivityId, activityId)
               .eq(ActivityRecord::getSeniorId, seniorId);
        Long count = activityRecordMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error(400, "您已签到过该活动，请勿重复签到");
        }

        User user = userMapper.selectById(seniorId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setSeniorId(seniorId);
        record.setStatus("signed");
        record.setSignInTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        activityRecordMapper.insert(record);

        if (activity.getRewardPoints() != null && activity.getRewardPoints() > 0) {
            int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
            user.setPoints(currentPoints + activity.getRewardPoints());
            userMapper.updateById(user);
        }

        return Result.success("签到成功，已发放" + (activity.getRewardPoints() != null ? activity.getRewardPoints() : 0) + "积分");
    }
}

package com.elderly.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityRecordMapper activityRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public int attendActivity(Long activityId, Long seniorId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            return 0;
        }

        QueryWrapper<ActivityRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId).eq("senior_id", seniorId);
        Long count = activityRecordMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return -1;
        }

        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setSeniorId(seniorId);
        record.setStatus("signed_in");
        record.setSignInTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        activityRecordMapper.insert(record);

        if (activity.getRewardPoints() != null && activity.getRewardPoints() > 0) {
            User user = userMapper.selectById(seniorId);
            if (user != null) {
                int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
                user.setPoints(currentPoints + activity.getRewardPoints());
                userMapper.updateById(user);
            }
        }

        return 1;
    }

    @Override
    public List<Long> getAttendedActivityIds(Long seniorId) {
        QueryWrapper<ActivityRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("senior_id", seniorId);
        List<ActivityRecord> records = activityRecordMapper.selectList(wrapper);
        return records.stream().map(ActivityRecord::getActivityId).collect(Collectors.toList());
    }
}

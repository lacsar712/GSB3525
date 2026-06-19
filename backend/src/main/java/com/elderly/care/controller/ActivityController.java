package com.elderly.care.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
import com.elderly.care.entity.ActivityRecord;
import com.elderly.care.mapper.ActivityRecordMapper;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRecordMapper activityRecordMapper;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(activityService.list());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Activity activity) {
        activityService.save(activity);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<?> records(@RequestParam Long seniorId) {
        LambdaQueryWrapper<ActivityRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRecord::getSeniorId, seniorId);
        List<ActivityRecord> records = activityRecordMapper.selectList(wrapper);
        List<Long> activityIds = records.stream()
                .map(ActivityRecord::getActivityId)
                .collect(Collectors.toList());
        return Result.success(activityIds);
    }

    @PostMapping("/attend")
    public Result<?> attend(@RequestBody java.util.Map<String, Object> req) {
        Long activityId = Long.valueOf(req.get("activityId").toString());
        Long seniorId = Long.valueOf(req.get("seniorId").toString());
        return activityService.attend(activityId, seniorId);
    }
}

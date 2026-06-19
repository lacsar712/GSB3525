package com.elderly.care.controller;

import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long seniorId) {
        List<Activity> activities = activityService.list();
        if (seniorId != null) {
            List<Long> attendedIds = activities.stream()
                .filter(a -> activityService.hasAttended(a.getId(), seniorId))
                .map(Activity::getId)
                .collect(Collectors.toList());
            Map<String, Object> data = new HashMap<>();
            data.put("list", activities);
            data.put("attendedIds", attendedIds);
            return Result.success(data);
        }
        return Result.success(activities);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Activity activity) {
        activityService.save(activity);
        return Result.success();
    }

    @PostMapping("/attend")
    public Result<?> attend(@RequestBody java.util.Map<String, Object> req) {
        Long activityId = Long.valueOf(req.get("activityId").toString());
        Long seniorId = Long.valueOf(req.get("seniorId").toString());

        int result = activityService.attend(activityId, seniorId);
        if (result == -1) {
            return Result.error("活动不存在");
        }
        if (result == -2) {
            return Result.error("您已签过到，请勿重复操作");
        }
        Result<Integer> successResult = Result.success(result);
        successResult.setMessage("签到成功，已发放 " + result + " 积分");
        return successResult;
    }
}

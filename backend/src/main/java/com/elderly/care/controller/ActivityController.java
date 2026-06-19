package com.elderly.care.controller;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired private ActivityService activityService;

    @GetMapping("/list")
    public Result<?> list() { 
        return Result.success(activityService.list()); 
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Activity activity) {
        activityService.save(activity); 
        return Result.success();
    }

    @GetMapping("/attended")
    public Result<?> attended(@RequestParam Long seniorId) {
        List<Long> ids = activityService.getAttendedActivityIds(seniorId);
        return Result.success(ids);
    }

    @PostMapping("/attend")
    public Result<?> attend(@RequestBody java.util.Map<String, Object> req) {
        Long activityId = Long.valueOf(req.get("activityId").toString());
        Long seniorId = Long.valueOf(req.get("seniorId").toString());

        int result = activityService.attendActivity(activityId, seniorId);
        if (result == 0) {
            return Result.error("活动不存在");
        }
        if (result == -1) {
            return Result.error(409, "您已签到过该活动，不可重复签到");
        }
        return Result.success("签到成功，已发放积分");
    }
}

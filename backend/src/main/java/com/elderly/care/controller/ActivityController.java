package com.elderly.care.controller;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/attend")
    public Result<?> attend(@RequestBody Map<String, Object> req) {
        Object activityIdObj = req.get("activityId");
        Object seniorIdObj = req.get("seniorId");
        if (activityIdObj == null || seniorIdObj == null) {
            return Result.error(400, "活动ID与老人ID不能为空");
        }

        Long activityId;
        Long seniorId;
        try {
            activityId = Long.valueOf(activityIdObj.toString());
            seniorId = Long.valueOf(seniorIdObj.toString());
        } catch (NumberFormatException ex) {
            return Result.error(400, "参数格式错误");
        }

        try {
            Integer reward = activityService.attend(activityId, seniorId);
            Map<String, Object> data = new HashMap<>();
            data.put("rewardPoints", reward);
            Result<Object> result = Result.success(data);
            result.setMessage(reward != null && reward > 0
                    ? "签到成功，已发放 " + reward + " 积分"
                    : "签到成功");
            return result;
        } catch (IllegalStateException dup) {
            // 重复签到：返回明确错误码 409
            return Result.error(409, dup.getMessage());
        } catch (IllegalArgumentException bad) {
            return Result.error(400, bad.getMessage());
        }
    }
}

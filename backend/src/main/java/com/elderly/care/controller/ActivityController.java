package com.elderly.care.controller;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Activity;
import com.elderly.care.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired private ActivityService activityService;
    @Autowired private com.elderly.care.mapper.UserMapper userMapper;

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
    @org.springframework.transaction.annotation.Transactional
    public Result<?> attend(@RequestBody java.util.Map<String, Object> req) {
        Long activityId = Long.valueOf(req.get("activityId").toString());
        Long seniorId = Long.valueOf(req.get("seniorId").toString());
        
        Activity activity = activityService.getById(activityId);
        if (activity == null) return Result.error("活动不存在");
        
        com.elderly.care.entity.User user = userMapper.selectById(seniorId);
        if (user != null && activity.getRewardPoints() != null && activity.getRewardPoints() > 0) {
            user.setPoints( (user.getPoints() == null ? 0 : user.getPoints()) + activity.getRewardPoints() );
            userMapper.updateById(user);
        }
        return Result.success("签到成功，已发放积分");
    }
}

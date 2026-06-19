package com.elderly.care.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.care.entity.Activity;

public interface ActivityService extends IService<Activity> {
    /**
     * 现场签到：写入 act_activity_record 并发放积分。
     * 同一老人对同一活动只能成功签到一次，重复签到会抛出 IllegalStateException。
     *
     * @param activityId 活动ID
     * @param seniorId   老人(用户)ID
     * @return 本次签到发放的积分数（活动未配置奖励时返回 0）
     */
    Integer attend(Long activityId, Long seniorId);
}

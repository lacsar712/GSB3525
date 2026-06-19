package com.elderly.care.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.entity.User;
import com.elderly.care.entity.ServiceOrder;
import com.elderly.care.entity.PointsExchangeRecord;
import com.elderly.care.entity.TreeHole;
import com.elderly.care.entity.TreeHoleComment;
import com.elderly.care.mapper.UserMapper;
import com.elderly.care.mapper.ServiceOrderMapper;
import com.elderly.care.mapper.PointsExchangeRecordMapper;
import com.elderly.care.mapper.TreeHoleMapper;
import com.elderly.care.mapper.TreeHoleCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ServiceOrderMapper serviceOrderMapper;

    @Autowired
    private PointsExchangeRecordMapper pointsMapper;

    @Autowired
    private TreeHoleMapper treeHoleMapper;

    @Autowired
    private TreeHoleCommentMapper treeHoleCommentMapper;

    @GetMapping("/stats")
    public Result<?> getStats() {
        Map<String, Object> data = new HashMap<>();

        // 基础统计
        long seniorCount = userMapper.selectCount(new QueryWrapper<User>().eq("role", "SENIOR"));
        long orgCount = userMapper.selectCount(new QueryWrapper<User>().eq("role", "ORG_ADMIN"));
        long carerCount = userMapper.selectCount(new QueryWrapper<User>().eq("role", "CARER"));
        long orderCount = serviceOrderMapper.selectCount(null);

        // 社交统计
        long treeHoleCount = treeHoleMapper.selectCount(null);
        long treeHoleCommentCount = treeHoleCommentMapper.selectCount(null);

        // 积分统计
        int pointsIn = 0;
        int pointsOut = 0;
        
        List<PointsExchangeRecord> records = pointsMapper.selectList(null);
        for (PointsExchangeRecord r : records) {
            if (r.getPointsCost() != null) {
                pointsOut += r.getPointsCost();
            }
        }
        
        // 粗略计算积分收入 = 所有用户的当前总积分 + 已经被消耗掉的积分
        List<User> users = userMapper.selectList(new QueryWrapper<User>().isNotNull("points"));
        int currentTotalPoints = 0;
        for (User u : users) {
             if (u.getPoints() != null) {
                 currentTotalPoints += u.getPoints();
             }
        }
        pointsIn = currentTotalPoints + pointsOut;

        data.put("seniorCount", seniorCount);
        data.put("orgCount", orgCount);
        data.put("carerCount", carerCount);
        data.put("orderCount", orderCount);
        data.put("pointsIn", pointsIn);
        data.put("pointsOut", pointsOut);
        data.put("treeHoleCount", treeHoleCount);
        data.put("treeHoleCommentCount", treeHoleCommentCount);

        return Result.success(data);
    }

    @GetMapping("/org")
    public Result<?> getOrgStats() {
        Map<String, Object> data = new HashMap<>();
        
        long todayPendingOrders = serviceOrderMapper.selectCount(new QueryWrapper<ServiceOrder>().eq("status", "PENDING"));
        long inProgressOrders = serviceOrderMapper.selectCount(new QueryWrapper<ServiceOrder>().eq("status", "IN_PROGRESS"));
        long activeCarers = userMapper.selectCount(new QueryWrapper<User>().eq("role", "CARER"));
        
        List<ServiceOrder> evaluatedOrders = serviceOrderMapper.selectList(new QueryWrapper<ServiceOrder>().isNotNull("evaluate_score"));
        double totalScore = 0;
        int maxPossible = evaluatedOrders.size() * 5;
        for (ServiceOrder o : evaluatedOrders) {
            totalScore += o.getEvaluateScore();
        }
        
        long satisfaction = 100;
        if (maxPossible > 0) {
            satisfaction = Math.round((totalScore / maxPossible) * 100);
        }

        data.put("todayPendingOrders", todayPendingOrders);
        data.put("inProgressOrders", inProgressOrders);
        data.put("activeCarers", activeCarers);
        data.put("customerSatisfaction", satisfaction);

        return Result.success(data);
    }

    @GetMapping("/carer")
    public Result<?> getCarerStats(Long carerId) {
        if (carerId == null) return Result.error("缺少护理员ID");
        Map<String, Object> data = new HashMap<>();
        
        // 今日完成
        String todayStart = java.time.LocalDate.now().toString() + " 00:00:00";
        String todayEnd = java.time.LocalDate.now().toString() + " 23:59:59";
        long todayCompleted = serviceOrderMapper.selectCount(new QueryWrapper<ServiceOrder>()
                .eq("carer_id", carerId)
                .eq("status", "COMPLETED")
                .ge("sign_out_time", todayStart)
                .le("sign_out_time", todayEnd));

        // 评分 (总体拉平)
        List<ServiceOrder> evaluatedOrders = serviceOrderMapper.selectList(new QueryWrapper<ServiceOrder>()
                .eq("carer_id", carerId)
                .isNotNull("evaluate_score"));
        
        double avgScore = 5.0; // 默认满分
        if (!evaluatedOrders.isEmpty()) {
            double totalScore = 0;
            for (ServiceOrder o : evaluatedOrders) {
                totalScore += o.getEvaluateScore();
            }
            avgScore = totalScore / evaluatedOrders.size();
        }

        data.put("todayCompleted", todayCompleted);
        data.put("monthlyRating", String.format("%.1f", avgScore));

        return Result.success(data);
    }
}

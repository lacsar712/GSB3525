package com.elderly.care.controller;

import com.elderly.care.common.Result;
import com.elderly.care.entity.HealthRecord;
import com.elderly.care.service.HealthRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long seniorId) {
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        if (seniorId != null) {
            queryWrapper.eq("senior_id", seniorId);
        }
        queryWrapper.orderByDesc("measure_time");
        return Result.success(healthRecordService.list(queryWrapper));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody HealthRecord healthRecord) {
        healthRecordService.save(healthRecord);
        return Result.success();
    }
}

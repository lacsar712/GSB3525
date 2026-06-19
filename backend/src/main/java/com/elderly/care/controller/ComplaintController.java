package com.elderly.care.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.common.annotation.AdminLog;
import com.elderly.care.entity.Complaint;
import com.elderly.care.mapper.ComplaintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintMapper complaintMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String status) {
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(complaintMapper.selectList(queryWrapper));
    }

    @PostMapping("/resolve")
    @AdminLog("处理服务投诉")
    public Result<?> resolve(@RequestBody Complaint complaint) {
        Complaint existing = complaintMapper.selectById(complaint.getId());
        if (existing == null) return Result.error("记录不存在");
        
        existing.setStatus("RESOLVED");
        existing.setResult(complaint.getResult());
        complaintMapper.updateById(existing);
        return Result.success();
    }
}

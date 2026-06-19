package com.elderly.care.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.entity.SysLog;
import com.elderly.care.mapper.SysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/log")
public class SysLogController {

    @Autowired
    private SysLogMapper sysLogMapper;

    @GetMapping("/list")
    public Result<?> list() {
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return Result.success(sysLogMapper.selectList(queryWrapper));
    }
}

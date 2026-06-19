package com.elderly.care.controller;

import com.elderly.care.common.Result;
import com.elderly.care.entity.ServiceOrder;
import com.elderly.care.service.ServiceOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class ServiceOrderController {

    @Autowired
    private ServiceOrderService serviceOrderService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long seniorId, @RequestParam(required = false) Long carerId) {
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        if (seniorId != null) queryWrapper.eq("senior_id", seniorId);
        if (carerId != null) queryWrapper.eq("carer_id", carerId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(serviceOrderService.list(queryWrapper));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody ServiceOrder serviceOrder) {
        serviceOrderService.save(serviceOrder);
        return Result.success();
    }
    
    @PutMapping("/update")
    public Result<?> update(@RequestBody ServiceOrder serviceOrder) {
        serviceOrderService.updateById(serviceOrder);
        return Result.success();
    }
}

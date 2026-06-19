package com.elderly.care.controller;

import com.elderly.care.common.Result;
import com.elderly.care.entity.PointsExchangeRecord;
import com.elderly.care.entity.PointsMall;
import com.elderly.care.entity.User;
import com.elderly.care.mapper.PointsExchangeRecordMapper;
import com.elderly.care.mapper.PointsMallMapper;
import com.elderly.care.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mall")
public class PointsMallController {

    @Autowired
    private PointsMallMapper pointsMallMapper;

    @Autowired
    private PointsExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public Result<List<PointsMall>> list() {
        return Result.success(pointsMallMapper.selectList(null));
    }

    @PostMapping("/exchange")
    @Transactional
    public Result<String> exchange(@RequestBody PointsExchangeRecord record) {
        User user = userMapper.selectById(record.getSeniorId());
        PointsMall mallItem = pointsMallMapper.selectById(record.getMallId());

        if (user == null || mallItem == null) {
            return Result.error("用户信息或商品不存在");
        }
        if (user.getPoints() == null || user.getPoints() < mallItem.getPointsRequired()) {
            return Result.error("积分不足");
        }
        if (mallItem.getStock() <= 0) {
            return Result.error("库存不足");
        }

        // Deduct points
        user.setPoints(user.getPoints() - mallItem.getPointsRequired());
        userMapper.updateById(user);

        // Deduct stock
        mallItem.setStock(mallItem.getStock() - 1);
        pointsMallMapper.updateById(mallItem);

        // Add record
        record.setPointsCost(mallItem.getPointsRequired());
        record.setCreateTime(LocalDateTime.now());
        exchangeRecordMapper.insert(record);

        return Result.success("兑换成功");
    }
}

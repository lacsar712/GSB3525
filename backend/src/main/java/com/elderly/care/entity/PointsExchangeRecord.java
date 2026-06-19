package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_points_exchange_record")
public class PointsExchangeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seniorId;
    private Long mallId;
    private Integer pointsCost;
    private LocalDateTime createTime;
}

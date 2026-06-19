package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_activity_record")
public class ActivityRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long seniorId;
    private String status;
    private LocalDateTime signInTime;
    private LocalDateTime createTime;
}

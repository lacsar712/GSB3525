package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seniorId;
    private String recordType;
    private String value1;
    private String value2;
    private Long measuredBy;
    private LocalDateTime measureTime;
    private Boolean isAbnormal;
    private String remark;
    private String measureStage;
    private Long relatedOrderId;
    private LocalDateTime createTime;
}

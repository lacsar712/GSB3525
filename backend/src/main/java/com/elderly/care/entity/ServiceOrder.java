package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("act_service_order")
public class ServiceOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seniorId;
    private Long childId;
    private Long serviceTypeId;
    private Long carerId;
    private String status;
    private LocalDateTime appointmentTime;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private BigDecimal price;
    private String remark;
    private Integer evaluateScore;
    private String evaluateText;
    private String evaluateImages;

    private Integer carerEvaluateScore;
    private String carerEvaluateText;
    private String carerEvaluateImages;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

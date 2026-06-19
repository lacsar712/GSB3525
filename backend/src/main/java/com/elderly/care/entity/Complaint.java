package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_complaint")
public class Complaint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String images;
    private Long complainantId;
    private Long targetId;
    private String targetType;
    private String status;
    private String result;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

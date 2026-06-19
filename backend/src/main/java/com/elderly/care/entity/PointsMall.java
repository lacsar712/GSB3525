package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_points_mall")
public class PointsMall {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer pointsRequired;
    private Integer stock;
    private String imageUrl;
    private LocalDateTime createTime;
}

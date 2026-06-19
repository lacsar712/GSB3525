package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String type;
    private String coverImage;
    private Integer views;
    private Integer likes;
    private LocalDateTime publishTime;
    private LocalDateTime scheduleTime;
    private String targetRole;
    private Boolean isPushed;
    private LocalDateTime createTime;
}

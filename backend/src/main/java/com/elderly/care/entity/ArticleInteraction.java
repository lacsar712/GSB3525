package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_article_interaction")
public class ArticleInteraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long userId;
    private Boolean isRead;
    private Boolean isLiked;
    private Boolean isBookmarked;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

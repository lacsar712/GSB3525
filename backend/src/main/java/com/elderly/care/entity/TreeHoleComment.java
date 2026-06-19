package com.elderly.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("act_tree_hole_comment")
public class TreeHoleComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long treeHoleId;
    private Long authorId;
    private String nickname;
    private String content;
    private LocalDateTime createTime;
}

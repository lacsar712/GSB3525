package com.elderly.care.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.entity.TreeHole;
import com.elderly.care.entity.TreeHoleComment;
import com.elderly.care.mapper.TreeHoleCommentMapper;
import com.elderly.care.mapper.TreeHoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/treehole")
public class TreeHoleController {

    @Autowired
    private TreeHoleMapper treeHoleMapper;

    @Autowired
    private TreeHoleCommentMapper treeHoleCommentMapper;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        QueryWrapper<TreeHole> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<TreeHole> holes = treeHoleMapper.selectList(wrapper);

        // Fetch comments inline for simplicity in this demo demo
        List<Map<String, Object>> result = holes.stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("treeHole", h);
            QueryWrapper<TreeHoleComment> commentWrapper = new QueryWrapper<>();
            commentWrapper.eq("tree_hole_id", h.getId()).orderByAsc("create_time");
            map.put("comments", treeHoleCommentMapper.selectList(commentWrapper));
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody TreeHole treeHole) {
        if (treeHole.getNickname() == null || treeHole.getNickname().isEmpty()) {
            treeHole.setNickname("匿名长者");
        }
        treeHole.setCreateTime(LocalDateTime.now());
        return Result.success(treeHoleMapper.insert(treeHole) > 0);
    }

    @PostMapping("/comment")
    public Result<Boolean> comment(@RequestBody TreeHoleComment comment) {
        if (comment.getNickname() == null || comment.getNickname().isEmpty()) {
            comment.setNickname("热心网友");
        }
        comment.setCreateTime(LocalDateTime.now());
        return Result.success(treeHoleCommentMapper.insert(comment) > 0);
    }

    @PostMapping("/like/{id}")
    public Result<Boolean> like(@PathVariable Long id) {
        TreeHole hole = treeHoleMapper.selectById(id);
        if (hole != null) {
            hole.setLikes(hole.getLikes() + 1);
            treeHoleMapper.updateById(hole);
            return Result.success(true);
        }
        return Result.error("记录不存在");
    }
}

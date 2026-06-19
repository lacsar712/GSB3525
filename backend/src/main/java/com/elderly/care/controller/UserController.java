package com.elderly.care.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.common.Result;
import com.elderly.care.common.annotation.AdminLog;
import com.elderly.care.dto.LoginDTO;
import com.elderly.care.entity.User;
import com.elderly.care.service.UserService;
import com.elderly.care.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            String token = JwtUtils.generateToken(user.getUsername(), user.getRole(), user.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", user);
            return Result.success(data);
        }
        return Result.error(401, "用户名或密码错误");
    }
    
    @GetMapping("/info")
    public Result<?> getInfo(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getById(userId));
    }

    @GetMapping("/admin/list")
    public Result<?> adminList(@RequestParam(required = false) String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq("role", role);
        }
        return Result.success(userService.list(queryWrapper));
    }

    @PostMapping("/admin/updateStatus")
    @AdminLog("更新用户状态")
    public Result<?> updateStatus(@RequestBody User user) {
        User existing = userService.getById(user.getId());
        if (existing == null) return Result.error("用户不存在");
        existing.setStatus(user.getStatus());
        userService.updateById(existing);
        return Result.success();
    }
}

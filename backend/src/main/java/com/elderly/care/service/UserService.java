package com.elderly.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.care.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
}

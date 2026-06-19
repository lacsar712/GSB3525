package com.elderly.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.care.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

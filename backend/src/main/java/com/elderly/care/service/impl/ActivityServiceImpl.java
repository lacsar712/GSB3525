package com.elderly.care.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.entity.Activity;
import com.elderly.care.mapper.ActivityMapper;
import com.elderly.care.service.ActivityService;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {}

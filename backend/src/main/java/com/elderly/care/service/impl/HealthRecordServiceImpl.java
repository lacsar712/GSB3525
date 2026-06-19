package com.elderly.care.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.entity.HealthRecord;
import com.elderly.care.mapper.HealthRecordMapper;
import com.elderly.care.service.HealthRecordService;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {
}

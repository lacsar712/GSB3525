package com.elderly.care.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.entity.ServiceOrder;
import com.elderly.care.mapper.ServiceOrderMapper;
import com.elderly.care.service.ServiceOrderService;
import org.springframework.stereotype.Service;

@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {
}

package com.elderly.care.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elderly.care.common.annotation.AdminLog;
import com.elderly.care.entity.SysLog;
import com.elderly.care.entity.User;
import com.elderly.care.mapper.SysLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AdminLogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Pointcut("@annotation(com.elderly.care.common.annotation.AdminLog)")
    public void logPointCut() {}

    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        saveLog(joinPoint);
    }

    private void saveLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AdminLog adminLog = method.getAnnotation(AdminLog.class);

        SysLog sysLog = new SysLog();
        if (adminLog != null) {
            sysLog.setAction(adminLog.value());
        }

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        Object[] args = joinPoint.getArgs();
        try {
            ObjectMapper mapper = new ObjectMapper();
            sysLog.setParams(mapper.writeValueAsString(args));
        } catch (Exception e) {
            sysLog.setParams("params serialize error");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setIp(request.getRemoteAddr());

        Object userId = request.getAttribute("userId");
        Object username = request.getAttribute("username");
        
        if (userId != null) {
            sysLog.setUserId(Long.valueOf(userId.toString()));
            sysLog.setUsername(username != null ? username.toString() : "Unknown");
        } else {
            sysLog.setUserId(0L);
            sysLog.setUsername("System/Anonym");
        }

        sysLog.setCreateTime(LocalDateTime.now());
        sysLogMapper.insert(sysLog);
    }
}

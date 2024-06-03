package com.zgg.store.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Component//将该类交由spring容器管理
@Aspect//标记当前类为切面类
public class TimeAspect {
    @Around("execution(* com.zgg.store.service.impl.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //先记录当前时间
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();//调用目标方法
        //后记录当前时间
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start));
        return result;
    }
}

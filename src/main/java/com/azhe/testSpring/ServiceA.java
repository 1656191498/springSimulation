package com.azhe.testSpring;

import com.azhe.mySpring.annotations.Autowired;
import com.azhe.mySpring.annotations.Component;

@Component
public class ServiceA {
    @Autowired
    ServiceB serviceB;

    @Autowired
    ServiceC serviceC;

    public void test(){
        System.out.println("测试代理方法");
        serviceC.test();
    }
}

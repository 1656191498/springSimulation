package com.azhe.testSpring;

import com.azhe.mySpring.annotations.Autowired;
import com.azhe.mySpring.annotations.Component;

@Component
public class ServiceB {
    @Autowired
    ServiceA serviceA;

    public void test(){
        System.out.println("执行B方法");
    }
}

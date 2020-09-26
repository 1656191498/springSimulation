package com.azhe.testSpring;

import com.azhe.mySpring.annotations.Autowired;
import com.azhe.mySpring.annotations.Component;

/**
 * @Description
 * @Author xwz
 * @Date 2020/9/26 10:03
 * @Version 1.0
 */
@Component
public class ServiceC {
    @Autowired
    ServiceB serviceB;

    public void test(){
        serviceB.test();
    }
}

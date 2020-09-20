package com.azhe.testSpring;

import com.azhe.mySpring.annotations.Autowired;
import com.azhe.mySpring.annotations.Component;

@Component
public class ServiceA {
    @Autowired
    ServiceB serviceB;
}

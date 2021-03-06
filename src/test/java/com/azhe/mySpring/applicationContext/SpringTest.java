package com.azhe.mySpring.applicationContext;

import com.azhe.testSpring.Appconfig;
import com.azhe.testSpring.ServiceA;
import com.azhe.testSpring.ServiceB;

/**
 * @Description 测试Spring
 * @Author xwz
 * @Date 2020/9/18 15:19
 * @Version 1.0
 */
public class SpringTest {
    public static void main(String[] args) {

        SpringApplicationContext springApplicationContext = new SpringApplicationContext(Appconfig.class);
        ServiceA bean = (ServiceA) springApplicationContext.doGetBean("serviceA");
        bean.test();
    }
}

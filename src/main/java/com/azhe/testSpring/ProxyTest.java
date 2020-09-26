package com.azhe.testSpring;

import com.azhe.mySpring.annotations.Component;
import com.azhe.mySpring.annotations.ProxyScan;
import com.azhe.mySpring.cglib.ProxyTemplate;

/**
 * @Description 测试代理
 * @Author xwz
 * @Date 2020/9/25 17:06
 * @Version 1.0
 */
@Component
//public .* com.azhe.testSpring..Service*..*(.*)   public void com.azhe.testSpring.ServiceA.test()
@ProxyScan("com.azhe.testSpring.Service.*")
public class ProxyTest implements ProxyTemplate{
    @Override
    public void before() {
        System.out.println("执行之前方法");
    }

    @Override
    public void after() {
        System.out.println("执行之后方法");
    }
}

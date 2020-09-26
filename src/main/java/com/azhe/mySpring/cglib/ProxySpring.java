package com.azhe.mySpring.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Description 代理
 * @Author xwz
 * @Date 2020/9/24 17:17
 * @Version 1.0
 */
public class ProxySpring implements MethodInterceptor {

    private Map<Method, List<ProxyTemplate>> map;

    private Object target;

    public ProxySpring(Map<Method, List<ProxyTemplate>> map,Object target){
        this.map = map;
        this.target = target;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(map!=null){
            if(map.containsKey(method)){
                map.get(method).forEach(ProxyTemplate::before);
            }
        }
        Object result = methodProxy.invoke(this.target, objects);
        if(map!=null){
            if(map.containsKey(method)){
                List<ProxyTemplate> proxyTemplates = map.get(method);
                for (int i = proxyTemplates.size()-1;i >= 0 ;i--){
                    proxyTemplates.get(i).after();
                }
            }
        }
        return result;
    }
}

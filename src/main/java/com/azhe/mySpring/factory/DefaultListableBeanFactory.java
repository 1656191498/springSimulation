package com.azhe.mySpring.factory;

import com.azhe.mySpring.bean.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description bean工厂
 * @Author xwz
 * @Date 2020/9/18 14:43
 * @Version 1.0
 */
public class DefaultListableBeanFactory {
    /**
     * 单例池
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);
}

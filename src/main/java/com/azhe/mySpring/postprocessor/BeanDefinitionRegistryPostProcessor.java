package com.azhe.mySpring.postprocessor;

import com.azhe.mySpring.bean.BeanDefinition;

import java.util.Map;

/**
 * @Description TODO
 * @Author xwz
 * @Date 2020/9/18 15:57
 * @Version 1.0
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{
    void postProcessBeanDefinitionRegistry(Map<String, BeanDefinition> beanDefinitionMap);
}

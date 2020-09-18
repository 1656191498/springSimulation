package com.azhe.mySpring.postprocessor;

import com.azhe.mySpring.annotations.ComponentScan;
import com.azhe.mySpring.bean.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @Description 后置处理器
 * @Author xwz
 * @Date 2020/9/18 15:55
 * @Version 1.0
 */
@ComponentScan("com.azhe.mySpring")
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionMap) {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(Map<String, BeanDefinition> beanDefinitionMap) {
        beanDefinitionMap.forEach((key ,value) ->{
            //查看beandefinition上面的注解
            Annotation componentScan = value.getBeanClass().getDeclaredAnnotation(ComponentScan.class);
            if(componentScan !=null && componentScan instanceof ComponentScan){
                String value1 = ((ComponentScan) componentScan).value();
                System.out.println(value1);
            }
        });
    }
}

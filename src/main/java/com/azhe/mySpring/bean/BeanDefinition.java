package com.azhe.mySpring.bean;

import com.sun.istack.internal.Nullable;

/**
 * @Description bean定义
 * @Author xwz
 * @Date 2020/9/18 14:40
 * @Version 1.0
 */
public class BeanDefinition {

    /**
     * bean类,我们从改类中读取类的注解等信息
     */
    private volatile Class beanClass;

    public BeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class getBeanClass() {
        return beanClass;
    }
}

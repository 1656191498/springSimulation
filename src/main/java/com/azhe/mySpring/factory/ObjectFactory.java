package com.azhe.mySpring.factory;

/**
 * @Description 对象工厂
 * @Author xwz
 * @Date 2020/9/24 15:03
 * @Version 1.0
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}

package com.azhe.mySpring.annotations;

import java.lang.annotation.*;

/**
 * 注入类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
}

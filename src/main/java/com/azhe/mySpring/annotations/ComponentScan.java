package com.azhe.mySpring.annotations;

import java.lang.annotation.*;

/**
 * 扫描包
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    String value();
}

package com.jdh.fuhsi.portal.util.crypt;

import java.lang.annotation.*;

/**
 * @description:
 * @author: qinzhishen
 * @create: 2020-02-07 11:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface CryptField {

    String value() default "";

    boolean encrypt() default true;

    boolean decrypt() default true;
}

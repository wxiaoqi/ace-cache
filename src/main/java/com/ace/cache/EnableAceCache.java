package com.ace.cache;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ace
 * @create 2017/11/17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoConfiguration.class)
@Documented
@Inherited
public @interface EnableAceCache {
}

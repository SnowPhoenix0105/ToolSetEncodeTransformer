package top.snowphoenix.toolsetencodetransformer.aop;

import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequireAuthWithLevel {
    AuthLevel value();
}

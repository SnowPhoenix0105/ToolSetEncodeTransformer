package top.snowphoenix.toolsetencodetransformer.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 为Controller的方法的参数进行标注。
 * 如果该参数为{@code CurrentUserInfo}，且该方法或该Controller有{@code @RequireAuthWithLevel}
 * 则将该参数绑定为当前用户的信息。
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

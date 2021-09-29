package top.snowphoenix.toolsetencodetransformer.aop;

import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;

import java.lang.annotation.*;


/***
 * 表示该方法/该Controller下的所有方法需要用户登录，并带有Authorization的header。
 * 该header内容应当为JWT，其中带有用户的uid和auth，并且可以通过公钥的校验。
 *
 * 添加该注解后，Controller或其方法，可以通过{@code @CurrentUser}来标注一个{@code CurrentUserInfo}类型的参数，
 * 此时将把当前用户的信息传入。
 *
 * 如果没有Authorization头，或JWT校验不通过（公钥验证不通过、超时等），则返回401，并且不会执行Controller的方法。
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequireAuthWithLevel {
    AuthLevel value();
}

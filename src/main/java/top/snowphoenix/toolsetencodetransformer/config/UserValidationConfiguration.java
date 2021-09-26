package top.snowphoenix.toolsetencodetransformer.config;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.snowphoenix.toolsetencodetransformer.aop.CurrentUserHandlerMethodArgumentResolver;
import top.snowphoenix.toolsetencodetransformer.aop.UserValidationInterceptor;

import java.util.List;

@Configuration
public class UserValidationConfiguration implements WebMvcConfigurer {
    public UserValidationConfiguration(UserValidationInterceptor userValidationInterceptor) {
        this.userValidationInterceptor = userValidationInterceptor;
    }

    private final UserValidationInterceptor userValidationInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(userValidationInterceptor)
                .addPathPatterns("/**")
                ;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers
                .add(new CurrentUserHandlerMethodArgumentResolver())
                ;
    }
}

package top.snowphoenix.toolsetencodetransformer.aop;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;
import top.snowphoenix.toolsetencodetransformer.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserValidationInterceptor implements HandlerInterceptor {
    public UserValidationInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public static final String AUTH_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    private AuthLevel getMinLevel(HandlerMethod handlerMethod) {
        var methodAnnotation = handlerMethod.getMethod().getAnnotation(RequireAuthWithLevel.class);
        if (methodAnnotation != null) {
            return methodAnnotation.value();
        }
        var classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireAuthWithLevel.class);
        if (classAnnotation == null) {
            return null;
        }
        return classAnnotation.value();
    }

    private void setUnauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        /*
         * TODO
         * add `WWW-Authenticate` header
         * see:
         * 1. https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status/401
         * 2. https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/WWW-Authenticate
         */
    }

    private CurrentUserInfo buildCurrentUserFromToken(String token) {
        if (token.startsWith(AUTH_PREFIX)) {
            token = token.substring(AUTH_PREFIX.length());
        }
        return jwtUtil.validateToken(token);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AuthLevel minLevel = getMinLevel(handlerMethod);
        if (minLevel == null) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null) {
            setUnauthorized(response);
            return false;
        }
        CurrentUserInfo userInfo;
        try {
            userInfo = buildCurrentUserFromToken(token);
        }
        catch (Exception e) {
            setUnauthorized(response);
            log.warn("build userInfo from token \"" + token + "\" fail: ", e);
            return false;
        }
        if (userInfo.getAuth().toNum() < minLevel.toNum()) {
            setUnauthorized(response);
            return false;
        }
        request.setAttribute(CurrentUser.class.getSimpleName(), userInfo);
        return true;
    }
}

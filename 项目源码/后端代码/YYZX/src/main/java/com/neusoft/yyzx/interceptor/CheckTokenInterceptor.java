package com.neusoft.yyzx.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CheckTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        //关于图片的静态资源的访问
        if (request.getRequestURI().toString().contains("/images")) {
            return true;
        }
        //获取token
        String token = request.getHeader("token");
        //检验token
        if (token == null) {
            throw new Exception("token为空，请登录！！！");
        } else {
            //校验token的有效性（正确性，时效性）
            JwtParser parser = Jwts.parser();
            //解析token的SigningKey必须和生成token时的密码一致
            parser.setSigningKey("neuedu123456");
            //如果token正确，则正常执行，否则抛出异常
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        }
        return true;
    }
}

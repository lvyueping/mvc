package com.wlkg.controller;

import com.wlkg.auth.entity.UserInfo;
import com.wlkg.auth.utils.JwtUtils;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.utils.CookieUtils;
import com.wlkg.config.JwtProperties;
import com.wlkg.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Value("${wlkg.jwt.cookieName}")
    private String cookieName;
    @Autowired
    private JwtProperties prop;


    @PostMapping("/login")
    public ResponseEntity<Void> login(
            // 无需给前端浏览器返回token，token只有后端才需要使用，并且将token保存到cookie中
            @RequestParam("username") String username, @RequestParam("password") String password,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 登录功能的实现
        String token = authService.login(username,password);
        // 将token写入cookie --- 工厂模式
        // httpOnly()：避免别的js代码来操作你的cookie，是一种安全措施
        // charset(): 不需要编码 因为token中没有中文
        // maxAge()： cookie的生命周期，默认是-1，代表一次会话，浏览器关闭cookie就失效
        // response: 将cookie写入 --- response中有一个方法 addCookie()
        // request: cookie中有域的概念 domain 例如一个cookie只能在www.baidu.com生效，无法在别的域下生效
        // 给cookie绑定一个域，防止别的网站访问你的cookie，也是一种安全措施
        CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName,token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/verify")
    public ResponseEntity<UserInfo> check(@CookieValue("WLKG_TOKEN") String token, HttpServletResponse response, HttpServletRequest request) throws Exception {

        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            String token1 = JwtUtils.generateToken(infoFromToken, prop.getPrivateKey(), prop.getExpire());
            CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName,token1);
            return ResponseEntity.ok(infoFromToken);
        } catch (Exception e) {
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
    }



}

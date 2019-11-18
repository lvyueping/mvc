package com.wlkg.user.controller;

import com.wlkg.user.pojo.User;
import com.wlkg.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验
     */
    /*    请求方式：GET
    - 请求路径：/check/{data}/{type}
    - 请求参数：param,type
    - 返回结果：true或false*/
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data, @PathVariable("type") Integer type){
        Boolean boo = userService.checkData(data,type);
        return ResponseEntity.ok(boo);

    }

    /**
     * 生成验证码
     */
   /*    请求方式：post
    - 请求路径：/code
    - 请求参数：phone
    - 返回结果：无*/
    @PostMapping("/code")
    public ResponseEntity<Void> makeCode(@RequestParam("phone")String phone){
        Boolean boo = userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 实现注册
     */
    /*    请求方式：post
    - 请求路径：/register
    - 请求参数：user code
    - 返回结果：无*/
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        userService.register(user,code);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 登录:根据用户名和密码查询用户
     */
 /*    请求方式：get
    - 请求路径：/query
    - 请求参数：username password
    - 返回结果：json*/
    @GetMapping("query")
    public ResponseEntity<User> login(@RequestParam("username") String username,@RequestParam("password") String password){
       User user = userService.queryOne(username,password);
       return ResponseEntity.ok(user);
    }


}

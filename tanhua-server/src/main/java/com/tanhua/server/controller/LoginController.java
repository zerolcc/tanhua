package com.tanhua.server.controller;


import com.tanhua.domain.db.User;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;

    /*
     * 根据手机号码查询用户
     * */

    @GetMapping("/findUser")
    public ResponseEntity<User> findUser(String mobile) {
        // 通过手机号码查询
        User user = userService.findByMobile(mobile);
        // 响应
        return ResponseEntity.ok(user);
    }

    /*
     * 新增用户
     * */
    @PostMapping("/saveUser")
    public ResponseEntity saveUser(@RequestBody Map<String, String> param) {
        String mobile = param.get("mobile");
        String password = param.get("password");
        // 保存
        userService.saveUser(mobile, password);
        return ResponseEntity.ok("保存成功");
    }

    /*
     * 登录时发送验证码
     * */
    @PostMapping("/login")
    public ResponseEntity sendValidateCode(@RequestBody Map<String, String> param) {
        //获取手机号码
        String phone = param.get("phone");
        //调用service发送
        userService.sendValidateCode(phone);
        //响应结果
        return ResponseEntity.ok(null);
    }


    /*
     * 登陆校验
     * */
    @PostMapping("loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String, String> param) {
        //获取手机号码
        String phone = param.get("phone");
        //获取过来的验证码
        String verificationCode = param.get("verificationCode");
        //验证登录
        Map<String,Object> result = userService.loginVerification(phone, verificationCode);
        //结果返回给apk
        return ResponseEntity.ok(result);
    }
}

package com.tanhua.server.service;

import com.google.errorprone.annotations.Var;
import com.sun.media.jfxmedia.logging.Logger;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.server.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2021/3/4
 */
@Service // 【注意】：这是消费者，不能使用dubbo的。
@Slf4j
public class UserService {

    @Reference
    private UserApi userApi;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 发送验证码工具类
     */
    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * redis业务前缀标识
     */
    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    public User findByMobile(String mobile) {
        return userApi.findByMobile(mobile);
    }

    public void saveUser(String mobile, String password) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        user.setCreated(new Date());
        userApi.save(user);
    }

    /**
     * 发送验证码
     *
     * @param phone
     */
    public void sendValidateCode(String phone) {
        //1. 构建redis中验证的key
        String key = redisValidateCodeKeyPrefix + phone;
        //2. 获取redis中验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(key);
        log.info("codeInRedis:{}", codeInRedis);

        if (StringUtils.isNotEmpty(codeInRedis)) {
            //3. 有值
            //  3.1 返回验证码未失效，抛出自定义异常
            throw new TanHuaException(ErrorResult.duplicate());
        } else {
            //4. 没有
            //5. 生成验证码
//            String validateCode = RandomStringUtils.randomNumeric(6);
            String validateCode = "000000";
            log.info("=============验证码:{},{}", phone, validateCode);
            //6. 发送验证码
            /*Map<String, String> smsRs = smsTemplate.sendValidateCode(phone, validateCode);
            if (null != smsRs) {
                 // 验证码发送失败，报错
                throw new TanHuaException(ErrorResult.fail());
            }*/
            //7. 存入redis
            redisTemplate.opsForValue().set(key, validateCode, Duration.ofMinutes(10));
        }
    }

    //登录验证
    public Map<String, Object> loginVerification(String phone, String verificationCode) {
        log.info("进入登录验证码的校验...");
        //1、验证校验码(删除redis中的验证码)
        //构建key
        String key = redisValidateCodeKeyPrefix + phone;
        //获取redis中的验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(key);
        log.info("验证码信息：{},{},{}....", phone, verificationCode, codeInRedis);
        //验证码的校验
        //2、不通过，报错
        //3、通过
        if (StringUtils.isBlank(codeInRedis)) {
            //验证码过时
            throw new TanHuaException(ErrorResult.loginError());
        }
        if (!codeInRedis.equals(verificationCode)) {
            //验证码不正确
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        //防止重复提交
        redisTemplate.delete(key);
        //4通过手机号码查询用户是否存在
        User user = userApi.findByMobile(phone);
        log.info("用户信息:{}", user == null ? "不存在" : "存在");
        //5、不存在、创建用户(调用user.api)
        //6、存在
        //isNew:false
        boolean isNew = false;
        if (null == user) {
            user = new User();
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setPassword("123456");
            user.setMobile(phone);
            //保存
            Long userId = userApi.save(user);
            //isNew:ture
            isNew = true; //x新建的
            user.setId(userId);
        }
        //7、创建token
        String token = jwtUtils.createJWT(phone, user.getId());
        log.info("创建的token{}", token);
        //8、存入redis（当用户再次请求到后台时，校验token有效性）
        redisTemplate.opsForValue().set("TOKEN_" + token, token, Duration.ofDays(7));
        //9、构建返回对象（token,isNew）
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("token", token);
        result.put("isNew", isNew);
        //10、返回给controller
        return result;
    }
}

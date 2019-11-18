package com.wlkg.user.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.utils.CodecUtils;
import com.wlkg.common.utils.NumberUtils;
import com.wlkg.user.mapper.UserMapper;
import com.wlkg.user.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "user:code:phone:";
    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        int i = userMapper.selectCount(user);
        return i==0;
    }

    /**
     * 生成验证码
     * @param phone
     * @return
     */
    public Boolean sendCode(String phone) {
        //生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            //发送短信
            Map<String,String> msg = new HashMap<>();
            msg.put("code",code);
            msg.put("phone",phone);
            amqpTemplate.convertAndSend("wlkg.sms.exchange","sms.verify.code",msg);
            //将验证码存入到redis
            redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 实现注册
     * @param user
     * @param code
     */
    /* - 1）校验短信验证码
        - 2）生成盐
        - 3）对密码加密
        - 4）写入数据库
        - 5）删除Redis中的验证码*/
    public void register(User user, String code) {
        String key = KEY_PREFIX+user.getPhone();
        String codeCache = this.redisTemplate.opsForValue().get(key);
        System.out.println(codeCache);
        System.out.println(code);
        if(!code.equals(codeCache)){
            throw new WlkgException(ExceptionEnums.INVALID_VERFIY_CODE);
        }
        user.setCreated(new Date());
        //生成盐
        String s = CodecUtils.generateSalt();
        user.setSalt(s);
        //对密码进行加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),s));
        //写入数据库
        Boolean boo = userMapper.insertSelective(user) ==1;
        //如果注册成功,删除redis中的code
        if(boo){
            try {
                redisTemplate.delete(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户名和密码实现登录
     * @param username
     * @param password
     * @return
     */
    public User queryOne(String username, String password) {
        User user = new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);
        if(user1==null){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        if(!user1.getPassword().equals(CodecUtils.md5Hex(password,user1.getSalt()))){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        return user1;
    }
}

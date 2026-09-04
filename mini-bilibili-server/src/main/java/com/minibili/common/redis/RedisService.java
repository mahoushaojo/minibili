package com.minibili.common.redis;

import cn.hutool.core.bean.BeanUtil;
import com.minibili.security.LoginSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// 创建redis的配置及通用方法
@Service
@RequiredArgsConstructor
public class RedisService {
    // 创建redis通用方法
    private final RedisTemplate<String,Object> redisTemplate;

    // 新增redis配置
    public void set(String key, Object value, Long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 获取redis配置
    public <T> T get(String key, Class<T> tClass){
        return BeanUtil.toBean(redisTemplate.opsForValue().get(key), tClass);
    }

    // 删除redis配置
    public void delete(String key){
        redisTemplate.delete(key);
    }
}

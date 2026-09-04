package com.minibili.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// 针对redis进行配置
@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String,Object> redisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper objectMapper
    ){

        RedisTemplate<String,Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);


        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(
                        objectMapper,
                        Object.class
                );


        template.setKeySerializer(
                new StringRedisSerializer()
        );


        template.setValueSerializer(serializer);


        template.setHashKeySerializer(
                new StringRedisSerializer()
        );


        template.setHashValueSerializer(serializer);


        template.afterPropertiesSet();

        return template;
    }

}

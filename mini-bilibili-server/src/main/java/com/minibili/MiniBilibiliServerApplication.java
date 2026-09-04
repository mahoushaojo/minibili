package com.minibili;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(
        basePackages = "com.minibili.module",
        annotationClass = Mapper.class
)

@SpringBootApplication
public class MiniBilibiliServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniBilibiliServerApplication.class, args);
    }
}

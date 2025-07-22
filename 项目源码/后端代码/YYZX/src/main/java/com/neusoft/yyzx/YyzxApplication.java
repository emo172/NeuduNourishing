package com.neusoft.yyzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//配置mapper的路径  mapper包扫描
@MapperScan(basePackages = "com.neusoft.yyzx.mapper")
public class YyzxApplication {
    public static void main(String[] args) {
        SpringApplication.run(YyzxApplication.class, args);
    }
}

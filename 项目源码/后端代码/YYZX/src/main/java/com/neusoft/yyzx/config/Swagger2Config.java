package com.neusoft.yyzx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket getDocket() {
        // 直接返回链式调用结果，消除冗余变量
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.neusoft.yyzx.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    // 提取封面信息构建逻辑为独立方法，提高代码可读性
    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("《东软颐养中心》后端接口说明")
                .description("《东软颐养中心》后端接口文档")
                .version("v 2.1.1")
                .contact(new Contact("吴昌涛", null, "2785703182@qq.com"))
                .build();
    }
}
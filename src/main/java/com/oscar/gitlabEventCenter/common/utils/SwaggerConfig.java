/**
 * Copyright © 2018天津神舟通用数据技术有限公司. All rights reserved.
 * @Title: SwaggerConfig.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.common.utils
 * @author: Yukai  
 * @date: 2018年6月5日 上午9:54:19
 */
package com.oscar.gitlabEventCenter.common.utils;

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

import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @ClassName: SwaggerConfig
 * @Description: TODO
 * @author Yukai
 * @data 2018年6月5日 上午9:54:19
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.oscar.gitlabEventCenter.web.controller"))
                .paths(doFilteringRules())
                .build()
                .apiInfo(apiInfo());
    }
    
    private Predicate<String> doFilteringRules() {
        return or(
                regex("/redmine.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gitlab && Redmine RestAPI")
                .description("将 Gitlab 相关项目的提交信息同步到 Redmine 相关 Issue")
                .termsOfServiceUrl("http://192.168.1.170:8899/private_tools/gitlabEventCenter")
                .contact(new Contact("yukai", "http://yukai.space/", "debiaoyu@gmail.com"))
                .version("1.0.0")
                .build();
    }
}

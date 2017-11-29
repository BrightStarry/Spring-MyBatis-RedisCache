package com.zx.springmybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zx.springmybatis.dao")
@EnableCaching
public class SpringMyBatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMyBatisApplication.class, args);
	}
}

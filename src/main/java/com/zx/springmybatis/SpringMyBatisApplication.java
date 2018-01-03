package com.zx.springmybatis;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zx.springmybatis.dao")
@EnableCaching
@Slf4j
public class SpringMyBatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMyBatisApplication.class, args);
		log.info("---------------------------------");
		log.warn("----------------------------------");
		log.debug("--------------------------------");
		log.error("------------------------------------");
	}
}

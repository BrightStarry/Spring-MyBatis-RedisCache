#### Spring整合MyBatis、通用Mapper
http://blog.csdn.net/gebitan505/article/details/54929287

##### 配置Mybatis
1. 引入依赖：
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>${mybatis-spring-boot-starter.version}</version>
		</dependency>
2. 在Application类上增加注解@MapperScan("com..zx.springmybatis.dao")，扫描dao层

3. 然后就可以直接在dao类上使用mybatis注解了

#### 配置Druid
4. 引入Druid依赖：
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>${druid.version}</version>
		</dependency>

5. 在yml文件中配置以spring.datasource开头的配置(具体配置参数可看DruidDataSource类源码)

6. 新建配置类，将DruidDataSource加入bean，并将yml中配置的参数注入
@Configuration
public class DruidDataSourceConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }
}

#### 配置Druid监控统计功能
7. 配置Servlet(原web.xml)：
@WebServlet(urlPatterns = "/druid/*",
initParams = {
        @WebInitParam(name = "allow",value = ""),// IP白名单 (没有配置或者为空，则允许所有访问)
        @WebInitParam(name = "deny",value = ""),// IP黑名单 (存在共同时，deny优先于allow)
        @WebInitParam(name = "loginUsername",value = "zx"),//用户名
        @WebInitParam(name = "loginPassword",value = "970389"),//密码
        @WebInitParam(name = "resetEnable",value = "false")// 禁用HTML页面上的“Reset All”功能
})
public class DruidStatViewServlet extends StatViewServlet{
}

8. 配置Filter:
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
        initParams={
                @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
        })
public class DruidStatFilter extends WebStatFilter{
}

9. 在Application类上加上@ServletComponentScan注解，让Servlet配置生效

10. 如下配置开启驼峰：
mybatis:
  configuration:
    #开启驼峰
    map-underscore-to-camel-case: true

#### Spring整合MyBatis、通用mapper、PageHelper
http://blog.csdn.net/gebitan505/article/details/54929287

* 如果出现无法读取yml文件的错误，检查yml文件的编码，删除所有中文即可

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
4. 引入Druid依赖:

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
#### 整合通用Mapper
1. 导入依赖：

		<dependency>
			<groupId>tk.mybatis</groupId>
			<artifactId>mapper-spring-boot-starter</artifactId>
			<version>${mapper-spring-boot-starter.version}</version>
		</dependency>

		<dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper-spring-boot-starter</artifactId>
			<version>${pagehelper-spring-boot-starter.version}</version>
		</dependency>
2. 创建MyMapper.java（注意。不能让@MapperScan("com.zx.springmybatis.dao")扫描到该类），其他所有mapper需要 ！继承 ！它。

        public interface CommonMapper<T> extends Mapper<T>,MySqlMapper<T> {
        }
        
3. 其他mapper继承他即可。

4. 以上，除了通用mapper，pagehelper也已经可以使用（ps：startPage方法后必须紧跟查询语句；返回的PageInfo中会包含许多分页信息）：
        
        public PageInfo<User> getAllForPage(Integer pageNum, Integer pageSize) {
                pageNum = pageNum == null ? 1 : pageNum;
                pageSize = pageSize == null ? 10 : pageSize;
        
                PageHelper.startPage(pageNum,pageSize);
                List<User> userList = userMapper.selectAll();
                PageInfo<User> pageInfo = new PageInfo<>(userList);
                
                log.info("pageInfo:{}",pageInfo);
                return pageInfo;
            }











#### 整合MyBatisPlus
1. 添加依赖:
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus</artifactId>
			<version>${mybatis-plus.version}</version>
		</dependency>

		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>${mybatis-spring-boot-starter.version}</version>
		</dependency>  
2. 

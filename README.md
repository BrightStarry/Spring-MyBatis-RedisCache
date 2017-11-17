#### Spring整合MyBatis、通用mapper、PageHelper
https://gitee.com/free/Mapper mapper主页
https://gitee.com/free/Mapper/blob/master/wiki/mapper3/5.Mappers.md   mapper所有方法
https://gitee.com/free/Mybatis_Utils/blob/master/MybatisGeneator/MybatisGeneator.md MybatisGeneator插件学习
http://blog.csdn.net/gebitan505/article/details/54929287


* 如果出现无法读取yml文件的错误，检查yml文件的编码，删除所有中文即可

##### 配置Mybatis
1. 引入依赖：(此处需要添加version的原因是，该jar是mybatis提供的，spring无法自动提供版本号)

		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>${mybatis-spring-boot-starter.version}</version>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>		
		
2. 在Application类上增加注解@MapperScan("com..zx.springmybatis.dao")，扫描dao层

3. 然后就可以直接在dao类上使用mybatis注解了

4. 如下配置开启驼峰：

        mybatis:
          configuration:
            #开启驼峰
            map-underscore-to-camel-case: true

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
2. 创建CommonMapper.java（注意。不能让@MapperScan("com.zx.springmybatis.dao")扫描到该类），其他所有mapper需要 ！继承 ！它。

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
5. 主键回写。在主键字段上增加@GeneratedValue(generator = "JDBC")这样的注解，还有uuid等，即可回写。  
该回写是在传入的实体对象中，原本为空的主键被赋值，而不是直接返回。

6. 注意：insertSelective()：保存一个实体，null的属性不会保存，会使用数据库默认值；  
insert():保存一个实体，null的属性也会保存，不会使用数据库默认值;  
update的方法也是一样。带Selective的才使用默认值 

7. Example使用：

                Example example = new Example(User.class)//传入实体类对象构造
                        .selectProperties("id", "name")//设置要查询的字段
                        .excludeProperties("id");//设置不查询的字段,与要查询同时设置，要查询的优先
                example.orderBy("id").desc();//排序
                example.createCriteria();//其他方法类似，基本都能用方法名理解
                        .andLessThan("id","4");//查询属性小于该值的记录
                        .andGreaterThan("id","4");//查询属性大于该值的记录
                        .andAllEqualTo(temp);//查询字段值等于该对象的属性值的记录，所有属性。
                        .andEqualTo(temp);//查询字段值等于该对象的属性值的记录，非空属性。
                        .andBetween("name","a","c");//between查询
                        .andCondition("name = 'a' or name ='b'");//可以直接使用sql查询，此处输入where后面的字符
        
        
                List<User> userList = userMapper.selectByExample(example);

#### 整合MyBatisGenerator
1. 在pom.xml中添加属性如下(注释的xml，是因为不想生成xml文件，直接用注解形式的):
>
        <!--  MyBatis Generator  -->
		<!--  Java接口和实体类  -->
		<targetJavaProject>${basedir}/src/main/java</targetJavaProject>
		<targetMapperPackage>tk.mybatis.mapper.mapper</targetMapperPackage>
		<targetModelPackage>tk.mybatis.mapper.model</targetModelPackage>
		<!--  XML生成路径  -->
		<!--<targetResourcesProject>${basedir}/src/main/resources</targetResourcesProject>-->
		<!--<targetXMLPackage>mapper</targetXMLPackage>-->
		<!--  依赖版本  -->
		<mapper.version>3.4.4</mapper.version>
		<mysql.version>5.1.44</mysql.version>
>

2. 增加maven插件，其参数由上面提供
>
            <plugin>
				<groupId>org.mybatis.generator</groupId>
				<artifactId>mybatis-generator-maven-plugin</artifactId>
				<version>1.3.5</version>
				<configuration>
					<configurationFile>${basedir}/src/main/resources/generator/generatorConfig.xml</configurationFile>
					<overwrite>true</overwrite>
					<verbose>true</verbose>
				</configuration>
				<dependencies>
					<dependency>
						<groupId>mysql</groupId>
						<artifactId>mysql-connector-java</artifactId>
						<version>${mysql.version}</version>
					</dependency>
					<dependency>
						<groupId>tk.mybatis</groupId>
						<artifactId>mapper</artifactId>
						<version>${mapper.version}</version>
					</dependency>
				</dependencies>
			</plugin>
>

3. 在resource下新增generator/generatorConfig.xml文件，其参数由下面的配置文件提供

4. 在同目录下新增config.properties文件

5. 在pom.xml这一级目录的命令行窗口执行mvn mybatis-generator:generate即可
    

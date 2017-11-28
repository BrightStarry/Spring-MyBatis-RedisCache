#### SpringBoot整合MyBatis/通用mapper/PageHelper,学习MyBatis注解/注解形式的动态sql等
https://gitee.com/free/Mapper mapper主页  
https://gitee.com/free/Mapper/blob/master/wiki/mapper3/5.Mappers.md   mapper所有方法  
https://gitee.com/free/Mybatis_Utils/blob/master/MybatisGeneator/MybatisGeneator.md MybatisGeneator插件学习  
http://blog.csdn.net/gebitan505/article/details/54929287  


* 如果出现无法读取yml文件的错误，检查yml文件的编码，删除所有中文即可

#### 奇淫巧技
* Guava CaseFormat:驼峰命名转换工具类

* !!!国人编写的一些框架千万不要傻逼的看jar中反编译的java代码,IDEA会提示你下载有javadoc的源码,下过来,  
中文注解注解起飞,舒服(例如这个通用Mapper)

* 使用MessageFormat可以将字符串中的若干标识符替换为指定文本.  
例如"My name is {}",可以将指定文本填充到{};  
或"My name {0} {1}",可以将一个String[]数组中的元素一次填充到{0},{1}


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
            
5. 如果需要使用mapper.xml,只需要在yml添加如下即可:
>
      mapper-locations: classpath:mapper/*.xml #xml文件内容
      type-aliases-package: com.zx.springmybatis.entity #实体类包
>

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

4. 以上，除了通用mapper，pageHelper也已经可以使用（ps：startPage方法后必须紧跟查询语句；返回的PageInfo中会包含许多分页信息）：
        
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
                
#### 输出MyBatisSQL语句
* 在yml中如下配置(com.zx.springmybatis.dao为自己的包名):
>
    # 输出MyBatis语句,trace会输出结果,debug只输出语句
    logging:
      level:
        com:
          zx:
            springmybatis:
              dao: debug
>
* 或使用logback,如该博客配置:http://blog.csdn.net/qincidong/article/details/76122727  
在logback.xml中配置:<logger name="mapper所在的包名" level="DEBUG"></logger>

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

5. 在pom.xml这一级目录的命令行窗口执行mvn mybatis-generator:generate即可(IDEA Terminal打开可直接在该目录运行)

#### MyBatis注解-动态sql的几种实现方式
1. 最原始-直接在方法注释上写动态sql代码:  
>
        @Insert("<script>INSERT INTO grade(name) values " +
                "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">" +
                "(#{item.name})" +
                "</foreach></script>")
        void addAll(List<Grade> grades);
>

2. 使用Provider和SQL语句构建器(若不适用构建器,自己手写sql也行):  
不使用SQL构建器:  
>
    /**
         * 使用Provider批量增加
         */
        @InsertProvider(type = Provider.class,method = "batchInsert")
        void addAll1(List<Grade> list);
    
        /**
         * 使用内部类作为Provider
         */
        class Provider{
            /**
             * 返回String作为sql语句
             * 不使用SQL构建器
             * 此处的sql是原生sql
             *
             * 参数: map中存储了MyBatisMapper方法中的参数;
             * 如果方法只有一个参数,也可以直接写相同类型的参数直接接收;
             * 如果方法使用了@Param注解,则使用map用@Param的value作为key接收
             * 如果多个参数,且未使用@Param注解,则使用map,用索引作为key接收
             * 具体可以下断点自行查看map
             */
            public String batchInsert(Map map) {
                List<Grade> list = (List<Grade>) map.get("list");
                StringBuilder result = new StringBuilder();
                result.append("INSERT INTO grade(name) VALUES");
                list.stream().forEach(item ->{
                    result.append("(").append("\"" + item.getName() + "\"").append(")");
                    result.append(",");
                });
                result.deleteCharAt(result.length()-1);
                return  result.toString();
            }
        }
>   
使用SQL构建器:  
SQL构建器使用教程(Mybatis官网): http://www.mybatis.org/mybatis-3/zh/statement-builders.html  
此处不作例子了,我只能说,这个构建器构建不是批量增加等操作的sql极其方便,但如果是批量增加等sql,还不如自己拼接呢;

3. 增强型注解.
* LanguageDriver接口:
    * createParameterHandler()方法:
        * 创建参数处理器,将预编译的sql的参数替换为真正内容,例如{name}/{1}这样的
    * createSqlSource(XNode)方法:
        * 创建SqlSource,它保存了从mapper.xml中读取出来的还未真正替换值的sql语句
    * createSqlSource(String)方法:它保存了从注解中读取出来的sql.
* 该接口的实现有XMLLanguageDriver,然后xml类还有个子类是RawLanguageDriver;
    * XMLLanguageDriver是未解析的也就是写在xml或注解中的那样的sql.
    * RawLanguageDriver是解析后的,可以直接执行的原生sql.(源码注解:除非确保是原生sql,否则没有任何理由使用该类)
* 自定义该接口:  
    * 如上介绍,我们可以通过继承XMLLanguageDriver类,重写createSqlSource(String)方法来实现自己的需求;
* 如下,就是我自己实现的一个通用的,可以对每个实体进行条件查询的扩展接口:
>
    /**
     * author:ZhengXing
     * datetime:2017/11/28 0028 14:19
     * 通用条件查询语言驱动
     */
    public class CommonConditionLanguageDriver extends XMLLanguageDriver{
        /**
         * 重写父类方法,以便在条件查询时,将不为空属性,加入where条件,
         * 例如:
         *   select * from user
         *   <where>
         *      <if test="username != null">and username=#{username}</if>
         *      <if test="password != null">and password=#{password}</if>
         *   </where>
         * parameterType:mapper中方法接收的参数,如果参数有多个,其值为map,当参数为多个时,无法获悉每个参数的类型(应该是)
         */
        @Override
        public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
            //追加where
            StringBuilder sql = new StringBuilder().append("<where>");
            //默认将该参数类型作为实体类类型处理,获取所有属性
            Field[] fields = parameterType.getDeclaredFields();
    
            //遍历实体类的每个属性
            for (Field field : fields) {
                //将java中 userId形式的属性转换为数据库中 user_id形式的
                String sqlField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                //循环增加的语句
                String temp = "<if test=\"javaField != null\">and sqlField=#{javaField} </if>";
                //将字符串中的自定义标识字符:javaField和sqlField替换
                temp = temp.replaceAll("javaField",field.getName())
                        .replaceAll("sqlField", sqlField);
                sql.append(temp);
            }
            sql.append("</where>");
    
            //增加<script>标签,表示该sql需要解析
            script = "<script>" + script +  sql.toString() + "</script>";
            //继续执行父类的方法实现,构建SqlSource
            return super.createSqlSource(configuration, script, parameterType);
        }
    }
>
在Mapper中如下写法:
>
        /**
         * 使用LanguageDriver进行通用的条件查询
         */
        @Lang(CommonConditionLanguageDriver.class)
        @Select("select * from user")
        List<User> findByCondition(User user);
>
如此调用,即可查询出所有 name=a,password=aa的记录,而其他空的字段则被忽略
>
            User user = new User().setName("a").setPassword("aa");
            List<User> a = userMapper.findByCondition(user);
            a.forEach(item-> System.out.println(a));
>
当然,这类通用的sql,在通用Mapper中都已经提供了.

#### 单表查询和多表关联查询的选择
* 一般来说,性能是多表占优.但是如果数据量大的话或许不一定.
* 多表查询如果关联表过多性能很低.
* 多表查询不方便使用缓存.
* 多表查询如果遇到分库分表等情况,需要重写sql
* 综上所述,推荐单表查询

#### SpringCache + redis 实现注解缓存


#### SpringBoot整合MyBatis/通用mapper/PageHelper,学习MyBatis注解/注解形式的动态sql等
https://gitee.com/free/Mapper mapper主页  
https://gitee.com/free/Mapper/blob/master/wiki/mapper3/5.Mappers.md   mapper所有方法  
https://gitee.com/free/Mybatis_Utils/blob/master/MybatisGeneator/MybatisGeneator.md MybatisGeneator插件学习  
http://blog.csdn.net/gebitan505/article/details/54929287  


#### 记录
* 在github逛到一个支付宝支付的无需申请支付宝api的项目...文档大略看了一遍就把项目撸下来了.
想看看它是如何实现..知道对方已经支付成功的...然后就看见...他妈的..对方创建订单后..通知管理员,
然后管理员打开自己的支付宝,通过比对金额和邮箱等信息,确认对方支付,手动修改状态....我的天..
算了...也算是一个可行的个人支付方案把..

#### bug
* 如果出现无法读取yml文件的错误，检查yml文件的编码，删除所有中文即可

#### 奇淫巧技
* 在github上随便看的xpay项目中的,比较不错的获取ip的方法.
>
    /**
         * 获取客户端IP地址
         * @param request 请求
         * @return
         */
        public static String getIpAddr(HttpServletRequest request) {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (ip.equals("127.0.0.1")) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ip = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ip != null && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
            return ip;
        }
>

* 使用logback后,让控制台恢复彩色日志(该操作在Spring Boot官方文档中有更详细的说明)
>
    logback.xml如下配置
    
     <!-- 彩色日志 -->
        <!-- 彩色日志依赖的渲染类 -->
        <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
        <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
        <conversionRule conversionWord="wEx"
                        converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
        <!-- 彩色日志格式 -->
        <property name="CONSOLE_LOG_PATTERN"
                  value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}" />
        <!-- Console 输出设置 -->
        <appender name="consoleLog" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>${CONSOLE_LOG_PATTERN}</pattern>
                <charset>utf8</charset>
            </encoder>
        </appender>
     
    yml增加如下配置
    spring:
      output:
        ansi:
          enabled: always
>

* SpringBoot默认日志格式:
> [%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-36.36thread] [%-5level] [%-36.36logger{36}:%-4.4line] - %msg%n

* IDEA, ctrl + backspace,快速删除

* 使用System.out.printf("cacheName:%s",item); 格式化输出.注意时后缀时tf

* IDEA/Spring Boot/yml文件中的属性中,按 CTRL + B ,可进入该属性注入的代码处..屌..无意中按了下

* 想到了一个lombok中@NonNull注解比较好的使用方式,只要在异常处理类中处理NullPointException,将其封装成自定义异常处理即可;  
这样,使用@NonNull注解后,就可以较为优雅地处理这类算是已经自己处理的异常了

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
		
2. 在Application类上增加注解@MapperScan("com.zx.springmybatis.dao")，扫描dao层

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

#### 配置Druid(需要注意的是,关于Druid的配置参数,去掉了许多没用的参数,具体参见其github上的文档,保留只是为了在替换时方便些(无需增减参数))
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
>
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
>

8. 修改操作的使用:
>
        public void updateGradeById(Long gradeId,Grade grade) {
    
            Example example = new Example(Grade.class);
            example.createCriteria().andEqualTo("id", gradeId);
    
            int i = gradeMapper.updateByExampleSelective(grade, example);
            //根据id直接更新
            //gradeMapper.updateByExampleSelective();
            System.out.println("更新条数:" + i);
        }
>
                
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
1. 引入spring redis和spring cache依赖:
>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
>

2. 在yml如下配置即可:
>
      #缓存
      cache:
        #缓存名字
        cache-names: #该属性的接收类型为list,得在这样写才可以分为一个个元素
          - a
          - b
          - c
        #缓存过期时间
        cacheExpires:  #自定义属性,也是list,用来配置缓存过期时间
          - 3600
          - 1
          - 0
        #缓存类型,同时引入guava包和redis时,不配置可能有bug
        type: redis
      #redis配置
      redis:
        host: 106.14.7.29
        port: 6379
        password: 970389
        pool:
          max-active: 10
          max-idle: 1
          min-idle: 0
          max-wait: 50000
>

3. 在Application类上增加:@EnableCaching注解(也就表示可用该注解一键关闭所有缓存)

4. 对所有需要缓存的对象需要实现Serializable接口

5. 此时,两次执行如下语句,第二次已经无需进行数据库查询,并且未进入方法体(其实现为AOP):
!!之前我一直以为其实现是AOP...后来我在@EnableCahcing注解中找到了..Mode参数,  
才发现其默认实现是代理类,当然可以选择用aop
>
        /**
         * 查询所有班级
         * 注意,@Cacheable中的cacheNames值需要在yml中配置,也就是spring.cache.cache-names
         */
        @Cacheable(value = "redis")
        public List<Grade> finAll() {
            log.info("查询所有班级");
            return gradeMapper.selectAll();
        }
>

6. 此时如果查看redis中的key的话,会发现该程序自动缓存的所有key,都有个redis:\xac\xed\x00\x05t\x00这样的前缀,  
其原因是使用了JDK默认的对象序列化方法Serializer<Object>.convert().而RedisTemplate<K,V>类的两个泛型为空,导致一些问题;  
只需要替换redis cache的默认序列化配置即可(其方法同样是在配置类中配置一个返回RedisTemplate类型的bean方法)(下面有介绍)

7. 自定义redis配置类,详见代码及其注释:
>
    /**
     * author:ZhengXing
     * datetime:2017/11/29 0029 13:32
     * redis缓存配置类
     *
     * CachingConfigurerSupport该类使用空方法实现了CachingConfigurer接口,
     * 子类只需要实现想要自定义的方法即可配置 缓存管理器/主键生成器/缓存解析器/异常处理器等;
     * 如果不实现该接口,配置该类后,还需在注解中指定对应的keyGenerator才能生效
     *
     */
    @Configuration
    public class RedisCacheConfig  extends CachingConfigurerSupport{
    
        //Spring构造的redis连接工厂
        @Autowired
        private RedisConnectionFactory redisConnectionFactory;
    
        //自定义的用来读取yml文件中每个缓存名对应的缓存过期时间的属性类
        @Autowired
        private CustomRedisCacheExpireProperties customRedisCacheExpireProperties;
    
        /**
         * 匿名内部类构建主键生成器
         * 其参数分别为 调用缓存的类(service)/调用缓存的方法/方法的参数列表
         */
        @Bean
        @Override
        public KeyGenerator keyGenerator() {
            return (object,method,params)->{
                //类名:方法名:参数[0]参数[1]...
                StringBuilder key = new StringBuilder(object.getClass().getSimpleName() + "-" + method.getName() + ":");
                for (Object param : params) {
                    //直接追加,只要该参数是基本类型或实现了toString方法,就没问题,否则会显示xx@hashcode那种类型的字符
                    //如果参数过多,需要自定义key
                    key.append(param.toString());
                }
                return key.toString();
            };
        }
    
        /**
         * 配置RedisTemplate
         * 是为了替换默认的JDK的序列化器,使用默认的序列化器,key会乱码;
         *
         * 此处在Spring中的实现是,他有一个默认的RedisTemplate Bean,但使用了
         * @ConditionalOnMissingBean(type = RedisTemplate.class)这样一个注解,
         * 表示在我们没有配置自定义的bean的情况下,才使用它默认的bean
         */
        @Bean
        public RedisTemplate redisTemplate() {
            //创建StringRedis模版
            StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
            // 使用Jackson2JsonRedisSerialize 替换默认序列化
            Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    
            //value使用jackJson序列化,key使用string序列化,string序列化不支持list等类型
            //stringRedisTemplate.setKeySerializer(new StringRedisSerializer());//不需要该设置,key也不会乱码.
            stringRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    
            //InitializingBean接口提供的一个方法,在spring容器属性被初始化完成后再调用该方法
            stringRedisTemplate.afterPropertiesSet();
    
            return  stringRedisTemplate;
        }
    
        /**
         * 创建缓存管理器
         * 主要为了自定义若干cacheNames和缓存过期时间;
         *
         * 自定义该类后,如果缓存注解中使用了一个未配置的缓存名,并且,该类的一个dynamic属性为true,
         * 就会生成一个新的以该名字为名的{@link Cache}对象,放入集合;
         * 但如果给该缓存管理器配置了cacheNames(也就是调用了setCacheNames()方法),该dynamic属性就会被
         * 设置为false,将无法动态加入缓存名;那么就会抛出无法找到该缓存的异常;
         * 我觉得还是设置上比较好.
         */
        @Bean
        @Override
        public CacheManager cacheManager() {
            RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
            //默认的过期时间,会被每个缓存名自己的过期时间覆盖
            redisCacheManager.setDefaultExpiration(3600);
            /**
             * 启动时加载远程缓存; 不开启:每次第一次查询即使缓存中已经有旧的缓存,也不会读取到;
             * 开启后如果缓存中已有缓存,第一次查询就会从缓存中读取
             */
            redisCacheManager.setLoadRemoteCachesOnStartup(true);
            //开启后,key会携带上cacheName作为前缀
            redisCacheManager.setUsePrefix(true);
            /**
             * 设置cacheNames,也可以在构造函数中设置,此处我使用在yml配置的cacheNames即可
             * 需要注意的是,显而易见,此处的RedisCacheManager还未注入yml中的cacheNames;
             * 所以如果使用redisCacheManager.getCacheNames()取出的将是空的;
             * 但是,如果使用setExpires()方法,设置好对应的cacheName和过期时间,还是能够生效的
             */
            //redisCacheManager.setCacheNames(Arrays.asList(cacheNames));
            //Collection<String> cacheNames = redisCacheManager.getCacheNames();
    
            //使用自定义的属性类,根据yml配置,生成缓存名和过期时间对应的map
            Map<String, Long> expires = customRedisCacheExpireProperties.generateExpireMap();
            //设置每个缓存对应的过期时间
            redisCacheManager.setExpires(expires);
            //给缓存管理器设置上缓存名s
            redisCacheManager.setCacheNames(customRedisCacheExpireProperties.getCacheNames());
    
    
            return redisCacheManager;
        }
    
        /**
         * 自定义缓存异常处理器.
         * 该CacheErrorHandler接口只有一个实现类SimpleCacheErrorHandler.只是抛出了所有异常未做任何处理
         *  有若干个方法,分别处理获取/修改/放入/删除缓存异常.
         *  若有需要.可自定义实现,比如因为缓存不是必须的,那就可以只做日志记录,不再抛出异常
         *
         */
        @Bean
        @Override
        public CacheErrorHandler errorHandler() {
           return  new SimpleCacheErrorHandler(){
                @Override
                public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                    super.handleCacheGetError(exception, cache, key);
                }
            };
        }
    
        /**
         * 自定义缓存解析器(该类必须是线程安全的)
         *
         * 其默认实现是SimpleCacheResolver
         *
         */
        @Override
        public CacheResolver cacheResolver() {
            return super.cacheResolver();
        }
    }
>

8. 自定义yml spring.cache属性类,详见代码及其注释:  
>
    /**
     * author:ZhengXing
     * datetime:2017/12/1 0001 12:46
     * 自定义的redis缓存中的过期时间属性
     */
    @Data
    @ConfigurationProperties(prefix = "spring.cache")
    @Component
    public class CustomRedisCacheExpireProperties {
        //该属性在spring cache框架自己的类中也会被获取
        //此处获取是为了对长度进行校验,防止 缓存名字 - 缓存时间 没有一一匹配
        private List<String> cacheNames;
    
        //缓存时间,和缓存名一一对应
        private List<Long> cacheExpires;
    
        /**
         * 生成Map,用来放入RedisManager中
         */
        public Map<String, Long> generateExpireMap() {
            Map<String, Long> expireMap = new HashMap<>();
            /**
             * 校验参数值
             */
            //如果未配置cacheNames属性,返回空map
            //如果未配置cacheExpires属性,也返回空map
            if (CollectionUtils.isEmpty(cacheNames) || CollectionUtils.isEmpty(cacheExpires))
                return expireMap;
            //长度校验:只要数组不为空,有x个cacheNames,就需要x个cacheExpires,如果某个name无需缓存时间,设置为0即可
            //其内部实现就是使用该Map生成若干个RedisCacheMetadata,该对象和cacheName一一对应,并且其中的默认过期时间就是0
            //不对.我在redis中试了下,将key过期时间设为0或负数,该key会直接过期.
            //找了很久..没找到其判断过期时间的代码
            if(cacheNames.size() != cacheExpires.size())
                //此处随便抛出一个非法状态异常,可自定义异常抛出
                throw new IllegalStateException("cacheExpires设置非法.cacheNames和cacheExpires长度不一致");
            //遍历cacheNames
            for (int i = 0; i < cacheNames.size(); i++) {
                //只有当cacheExpires设置的大于0时,才放入map
                long expire = cacheExpires.get(i);
                if (expire > 0)
                    expireMap.put(cacheNames.get(i),expire);
            }
            return expireMap;
        }
    }
>


#### SpringCache注解
* 注意:
    * spEl表达式如果不想使用,需要用两个单引号转移

* @CacheConfig:注解在类上,表示该类方法上的注解都默认使用该注解定义的配置;  
    配置该注解后,方法上的注解也可以配置自己的属性,覆盖该注解;  
    可配置cacheNames/keyGenerator/cacheManager/cacheResolver

* @Cacheable:(查询)注解在方法上,表示执行该方法前先从缓存中读取数据,没有再从方法中读取;
    * cacheNames: 缓存名,也就是配置在yml中的属性(如果不配置@CacheConfig,它是必须的)  
        需要注意的时,如果配置了自定义的RedisManager,即使RedisManager和yml中都没有配置的name也是可以使用的;
        研表究明...当配置了自定义的缓存管理器后,yml中的cacheNames不会在再被使用
    * key: 缓存的Key,可配置,不配置使用spring默认的SimpleKeyGenerator生成;  支持spEl表达式
        除了上面使用方法参数作为Key以外，Spring还为我们提供了一个root对象可以生成key。通过root对象我们还可以获取到  
        -------1.methodName  当前方法名    #root.methodName  
        -------2.method       当前方法  #root.method.name  
        -------3.target   当前被动用对象  #root.target  
        -------4.targetClass      当前被调用对象Class#root.targetClass  
        -------5.args    当前方法参数组成的数组  #root.args[0]  
        -------6.caches    当前被调用方法所使用的Cache  #root.caches[0],name  
        使用root作为key时，可以不用写root直接@Cache(key="caches[1].name"),他默认是使用#root的   
    * condition: 缓存对象的条件,非必须,SpEL表达式,只有满足条件的内容才会被缓存,  
        例如#param.length() < 3,表示参数param长度小于3时才被缓存;
    * unless: 另一个缓存条件参数,SpEL表达式,它不同于condition参数的地方在于它的判断时机，  
        该条件是在函数被调用之后才做判断的，所以它可以通过对result进行判断
    * keyGenerator: 指定key生成器;该参数和key参数互斥,配置了某一个就不能配置另一个;
    * cacheManager: 指定缓存管理器;
    * cacheResolver: 指定缓存解析器;
    * sync: 缓存为空时,如果多个线程同时调用底层方法(数据库),则线程阻塞的调用,尝试为相同的key加载同样的value.
        它会导致几个问题:1.不支持unless参数; 2.只能指定一个缓存; 3.不能与其他缓存相关的操作组合; 默认为false.
        它适用于那种高并发下的,某个缓存正好过期的场景.
        
* @CachePut:(更新)无论缓存是否存在,都会将执行结果放入缓存;
    用于insert方法,或update(如果时更新,需要将更新后的结果返回)

* @CacheEvict:(删除)删除指定缓存;用于删除或更新操作
    * 雷同参数不再赘述.自行查看
    * allEntries: 是否删除所有条目(整个cacheNames),默认只删除当前key.
        注意,当它为true时,不允许指定该注解的key参数
    * beforeInvocation: 是否在方法调用前删除;
        设置为true,无论结果如何该缓存都会被删除,(例如当方法异常);  
        默认为false,也就是当该方法执行成功之后才会删除缓存(如果抛出异常,则不会删除)
        
* @Caching:使用该注解在同一个方法上叠加多个缓存注解;
    该注解的成员变量如下(我就不想再说什么了,一目了然):
    >
        	Cacheable[] cacheable() default {};
        	CachePut[] put() default {};
        	CacheEvict[] evict() default {};
    >

* 自定义注解:只需要在注解类上增加上面这些注解,再将注解类注解到方法上,一样可以


#### SpringCache使用设想
对于缓存的使用,之前我觉得有一些问题.  
例如,有一个根据id查询user的方法使用缓存;
那么,如果有一个修改user的方法,使用@CachePut注解,将修改后的值直接放入缓存.  
或者其他类似的场景,需要在方法中,修改其他方法需要读取的缓存.
就需要将@CachePut/@CacheEvict上的注解上的key和@Cacheable上的key对应起来;  
  
例如我目前的写法,根据简单类名/方法名/参数值生成缓存.  
我的查询方法是 CacheService类的findOneByGradeId方法.  
就需要在新增缓存值的方法上这样写:@CachePut(key = "'CacheService-findOneByGradeId-' + #result.id")  
那如果我需要修改类名/方法名等,岂不是爆炸了.  

然后我突然顿悟.他是有个cacheNames的,可配置多个不同的缓存前缀;  
那么,我就可以将每个类或有关联的几个缓存方法,设置上各自的cacheName.  
然后将缓存的key都改为简单的可动态编写的.例如几个参数的hashcode等.  
(或者直接每个缓存关联使用一个cacheName也可,只是这样名字的数量可能会很多)  
然后,在缓存配置类的缓存管理器中不再设置缓存名集合,这样就可以动态生成缓存名了.  
然后如果不需要默认过期时间的缓存,照旧可以在yml中自定义过期时间.  

再或者,可以自定义一个注解,注解在类上,包含了类中的cacheName和其过期时间,  
然后就可以在启动时扫描所有类,解析出数据,放入缓存管理器中.

          
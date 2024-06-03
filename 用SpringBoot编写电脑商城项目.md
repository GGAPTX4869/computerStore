## 项目环境

JDK1.8	Maven3.8.3	Tomcat9.0.54	Mysql8.0	

技术栈：springboot+mybatis+mysql+html+javascript+css+json+jquery+ajax+HikariProxy

**项目简介：**

一款电脑商城项目，主要包括用户、商品、商品类别、收藏、订单、购物车、收货地址等模块功能。项目整体通过json+ajax进行数据传递，实现前端页面的异步数据更新，用户注册账户时密码采用盐值加密存储至数据库，实现了增删查改、异常获取、拦截器、aop统计业务方法耗时功能。

## 框架搭建

1. 新建一个SpringBoot项目，项目名为store

2. 选择导入的jar包

   我这边选择lombok、Spring Web、JDBC API、MyBatis Framework、MSQL Driver

3. 测试项目是否能够正常运行（访问http://localhost:8080/）

4. 创建application.yml，配置数据库的信息

   ```yml
   # 数据库配置
   spring:
     datasource:
       username: root
       password: root
       url: jdbc:mysql://localhost:3306/store?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
       driver-class-name: com.mysql.cj.jdbc.Driver
   # mybatis配置
   mybatis:
     type-aliases-package: com.zgg.store.entity
     mapper-locations: classpath:mapper/*.xml
     configuration:
       map-underscore-to-camel-case: true
   ```

5. 创建数据库store

6. 在测试类中测试数据库是否连接成功

   > 使用默认的Hikari数据源

   ```java
   @SpringBootTest
   class StoreApplicationTests {
   
       @Autowired
       private DataSource dataSource;//自动装配
   
       @Test
       void getConnection() throws SQLException {
           System.out.println(dataSource.getConnection());
       }
   
   }
   ```

7. 测试静态资源是否能够正常访问，将所有的静态资源复制到static目录下

   > 如果js代码不能够正常去访问可以尝试以下操作
   >
   > 1. idea清理缓存
   > 2. clear-install
   > 3. rebuild重新构建
   > 4. 重启idea和电脑

## 功能实现

### 用户注册

#### 编写数据库

```sql
CREATE DATABASE store CHARACTER SET utf8;

CREATE TABLE t_user (
	uid INT AUTO_INCREMENT COMMENT '用户id',
	username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
	PASSWORD CHAR(32) NOT NULL COMMENT '密码',
	salt CHAR(36) COMMENT '盐值',
	phone VARCHAR(20) COMMENT '电话号码',
	email VARCHAR(30) COMMENT '电子邮箱',
	gender INT COMMENT '性别:0-女，1-男',
	avatar VARCHAR(50) COMMENT '头像',
	is_delete INT COMMENT '是否删除：0-未删除，1-已删除',
	created_user VARCHAR(20) COMMENT '日志-创建人',
	created_time DATETIME COMMENT '日志-创建时间',
	modified_user VARCHAR(20) COMMENT '日志-最后修改执行人',
	modified_time DATETIME COMMENT '日志-最后修改时间',
	PRIMARY KEY (uid)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

#### 编写实体类

1. 通过表的结构提取除表的公共字段，放在一个实体类的基类中

   ```java
   //作为实体类的基类
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class BaseEntity implements Serializable {
       private String createdUser;//日志-创建人
       private Date createdTime;//日志-创建时间
       private String modifiedUser;//日志-最后修改执行人
       private Date modifiedTime;//日志-最后修改时间
   }
   ```

2. 创建用户的实体类。需要继承BaseEntity基类

   ```java
   //用户的实体类
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   @Component
   public class User extends BaseEntity implements Serializable {
       private Integer uid;//用户id
       private String username;//用户名
       private String password;//密码
       private String salt;//盐值
       private String phone;//电话号码
       private String email;//电子邮箱
       private Integer gender;//性别:0-女，1-男
       private String avatar;//头像
       private Integer isDelete;//是否删除：0-未删除，1-已删除
   }
   ```

#### 注册-持久层

> 需要在启动类中配置扫描包
>
> ```java
> @MapperScan("com.zgg.store.mapper")
> ```

- 创建UserMapper接口

  ```java
  public interface UserMapper {
      /**
       * 插入用户的数据
       * @param user 用户的数据
       * @return 受影响的行数
       */
      Integer insert(@Param("user") User user);
  
      /**
       * 根据用户名来查询用户的数据
       * @param username 用户名
       * @return 如果找到对应的用户则返回这个用户丶数据，如果没有找到则返回null
       */
      User findByUsername(@Param("username") String username);
  }
  
  ```

- 在resources目录下创建mapper包，创建UserMapper.xml

  ```java
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.zgg.store.mapper.UserMapper">
      <insert id="insert" parameterType="User" useGeneratedKeys="true" keyProperty="uid">
          insert into t_user (
              username,password,salt,phone,email,gender,avatar,is_delete,
              created_user,created_time,modified_user,modified_time)
          values (#{username},#{password},#{salt},#{phone},#{email},#{gender},#{avatar},#{isDelete},
                  #{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})
      </insert>
  
      <select id="findByUsername" resultType="User">
          select * from t_user where username=#{username}
      </select>
  </mapper>
  ```

- 测试

  在测试目录下创建mapper包，创建UserMapperTests测试类

  ```java
  //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
  @SpringBootTest
  //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
  @RunWith(SpringRunner.class)
  public class UserMapperTests {
      @Autowired
      UserMapper userMapper;
  
      @Test
      public void insert(){
          User user = new User();
          user.setUsername("tim");
          user.setPassword("123");
          Integer insert = userMapper.insert(user);
          System.out.println(insert);
      }
  
      @Test
      public void findByUsername(){
          User tim = userMapper.findByUsername("tim");
          System.out.println(tim);
      }
  
  }
  ```


#### 注册-业务层

> 规划异常
>
> 设计接口
>
> 实现接口

1. 创建service目录，其目录下创建ex、impl包

2. 编写业务层可能出现的异常

   > alt+ins选择全部的构造方法即可

   UsernameDuplicatedException

   ```java
   /**
    * <p>
    *用户名被占用的异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   public class UsernameDuplicatedException extends ServiceException{
       public UsernameDuplicatedException() {
       }
   
       public UsernameDuplicatedException(String message) {
           super(message);
       }
   
       public UsernameDuplicatedException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public UsernameDuplicatedException(Throwable cause) {
           super(cause);
       }
   
       public UsernameDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   ServiceException

   ```java
   /**
    * <p>
    *业务层异常的基类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   public class ServiceException extends RuntimeException{
       public ServiceException() {
       }
   
       public ServiceException(String message) {
           super(message);
       }
   
       public ServiceException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public ServiceException(Throwable cause) {
           super(cause);
       }
   
       public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   InsertException

   ```java
   /**
    * <p>
    *数据在插入过程中所产生的异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   public class InsertException extends ServiceException{
       public InsertException() {
       }
   
       public InsertException(String message) {
           super(message);
       }
   
       public InsertException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public InsertException(Throwable cause) {
           super(cause);
       }
   
       public InsertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

3. 编写业务层接口

   ```java
   /**
    * <p>
    *用户模块业务层接口
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   public interface UserService {
       /**
        * 用户注册方法
        * @param user 用户的数据
        */
       void reg(User user);
   
   }
   ```

4. impl包下编写实现类

   > 密码使用md5算法加密保存至数据库

   ```java
   /**
    * <p>
    *用户模块业务层的实现类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   @Service
   public class UserServiceImpl implements UserService {
       @Autowired
       private UserMapper userMapper;
   
       //定义一个md5算法的加密处理
       private String getMD5Password(String password,String salt){
           //md5加密算法方法的调用
           for (int i = 0; i < 3; i++) {
               password=DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
           }
           //返回加密之后的密码
           return password;
       }
   
       @Override
       public void reg(User user) {
           //调用findByUsername判断用户是否注册过
           User result = userMapper.findByUsername(user.getUsername());
           if(result!=null){
               //抛出异常
               throw new UsernameDuplicatedException("用户名被占用");
           }
           //密码加密处理的实现：md5算法的形式
           //（串+password+串）
           String oldPassword = user.getPassword();
           String salt = UUID.randomUUID().toString().toUpperCase();
           //补全数据：盐值记录
           user.setSalt(salt);
           String md5Password = getMD5Password(oldPassword, salt);
   
           user.setPassword(md5Password);
   
           //补全数据：is_delete设置为0，四个日志字段信息
           user.setIsDelete(0);
           user.setCreatedUser(user.getUsername());
           user.setModifiedUser(user.getUsername());
           Date date = new Date();
           user.setCreatedTime(date);
           user.setModifiedTime(date);
           //执行注册
           Integer rows = userMapper.insert(user);
           if(rows !=1){
               throw new InsertException("在用户注册过程中产生了未知的异常");
           }
   
       }
   }
   ```

5. 测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class UserServiceTests {
       @Autowired
       UserService userService;
   
       @Test
       public void reg(){
           try {
               User user = new User();
               user.setUsername("zgg2");
               user.setPassword("123");
               userService.reg(user);
           }catch (ServiceException e){
               System.out.println(e.getClass().getSimpleName());
               System.out.println(e.getMessage());
           }
   
       }
   
   }
   ```

#### 注册-控制层

1. 创建util包编写工具类JsonResult，便于返回Json数据

   ```java
   /**
    * <p>
    *Json格式的数据进行响应
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   @Data
   @NoArgsConstructor
   public class JsonResult<E> implements Serializable {
       //状态码
       private Integer state;
       //描述信息
       private String message;
       //数据
       private E data;
   
       public JsonResult(Integer state) {
           this.state = state;
       }
   
       public JsonResult(Throwable e) {
           this.message = e.getMessage();
       }
   
       public JsonResult(Integer state, E data) {
           this.state = state;
           this.data = data;
       }
   }
   ```

2. 封装编写一个基类控制器来捕获异常并返回数据

   ```java
   public class BaseController {
       //操作成功的状态码
       public static final int OK = 200;
   
       //请求处理方法，这个方法的返回值就是需要传递给前端的数据
       //自动将异常对象传递给此方法的参数列表上
       //当前项目中产生了异常，被统一拦截到此方法中，这个方法此时就充当的是请求处理方法，方法的返回值直接给前端
       @ExceptionHandler(ServiceException.class)//统一处理抛出的异常
       public JsonResult<Void> handleException(Throwable e){
           JsonResult<Void> result = new JsonResult<>(e);
           if(e instanceof UsernameDuplicatedException){
               result.setState(4000);
               result.setMessage("用户名已经被占用");
           }else if(e instanceof InsertException){
               result.setState(5000);
               result.setMessage("注册时产生未知的异常");
           }
           return result;
       }
   
   }
   ```

3. 编写UserController

   ```java
   @RestController
   @RequestMapping("/users")
   public class UserController extends BaseController{//继承基类
       @Autowired
       private UserService userService;
   
       @RequestMapping("/reg")
       public JsonResult<Void> reg(User user){
           userService.reg(user);
           //如果userService.reg(user)没有产生异常则会正常往下走，否则会走到BaseController类中的异常方法处理中
           return new JsonResult<>(OK);
       }
   }
   ```

4. 测试http://localhost:8080/users/reg?username=tom002&password=111111

   此时去数据库中可以查看是否成功

#### 注册-前端页面

1. html页面注册自带表单代码如下

   ```html
   <form id="form-reg" class="form-horizontal" role="form">
      <!--用户名-->
      <div class="form-group">
         <label class="col-md-3 control-label">名字：</label>
         <div class="col-md-8">
            <input name="username" type="text" class="form-control" placeholder="请输入用户名">
         </div>
      </div>
      <!--密码-->
      <div class="form-group">
         <label class="col-md-3 control-label"> 密码：</label>
         <div class="col-md-8">
            <input name="password" type="text" class="form-control" placeholder="请输入密码">
         </div>
      </div>
      <!--确认密码-->
      <div class="form-group">
         <label class="col-md-3 control-label"> 确认密码：</label>
         <div class="col-md-8">
            <input type="text" class="form-control" placeholder="请再次输入密码">
         </div>
      </div>
      <!--提交按钮-->
      <div class="form-group">
         <label class="col-md-3 control-label"></label>
         <div class="col-md-8">
            <input id="btn-reg" class="btn btn-primary" type="button" value="立即注册" />
            <span class="pull-right"><small>已经有账号？</small><a href="login.html">登录</a></span>
         </div>
      </div>
   </form>
   ```

2. 编写ajax完成注册操作

   > 在使用script时，一定要在body末尾编写，尽量不要在head里编写，否则可能出现无法访问的情况
   >
   > 如果放在末尾还出现无法访问的情况，可以参考框架搭建中的静态资源无法访问解决方法

   ```html
   <script type="text/javascript">
      //1.监听注册按钮是否被点击
      $("#btn-reg").click(function (){
         //通过id选择器动态获取表单中控制的数据
         let username=$("#username").val();
         let pwd=$("#password").val();
         console.log($("#form-reg").serialize());
         //2.发送ajax()的异步请求,完成用户的注册功能
         $.ajax({
            url: "/users/reg",
            type: "POST",
            //直接通过id选择器的序列化获取表单内的数据
            //username=Tom&password=123
            data:$("#form-reg").serialize(),
            // data: {
            //     "username":username,
            //     "password":pwd
            // },
            dataType: "JSON",
            success: function (json){
               if(json.state == 200){
                  alert("注册成功");
               }else{
                  alert("注册失败");
               }
            },
            error: function (xhr){
               alert("注册时产生未知的错误！"+xhr.status);
            }
         });
      });
   </script>
   ```

3. 测试


### 用户登录

#### 登录-持久层

> 登录页面的持久层可以使用注册页面的语句

1. UserMapper

   ```java
   /**
    * 根据用户名来查询用户的数据
    * @param username 用户名
    * @return 如果找到对应的用户则返回这个用户丶数据，如果没有找到则返回null
    */
   User findByUsername(String username);
   ```

2. UserMapper.xml

   ```xml
   <select id="findByUsername" resultType="User">
       select * from t_user where username=#{username}
   </select>
   ```

#### 登录-业务层

1. 规划异常

   > 业务层异常需要继承前面编写的ServiceException异常类

   1. 密码匹配失败的异常，运行时异常，业务异常

      ```java
      /**
       * <p>
       *密码匹配失败的异常
       * </p>
       *
       * @autor:zgg
       * @date:2022/3/17
       */
      public class PasswordNotMatchException extends ServiceException{
          public PasswordNotMatchException() {
          }
      
          public PasswordNotMatchException(String message) {
              super(message);
          }
      
          public PasswordNotMatchException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public PasswordNotMatchException(Throwable cause) {
              super(cause);
          }
      
          public PasswordNotMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

   2. 用户名没有被找到的异常，运行时异常，业务异常

      ```java
      /**
       * <p>
       *用户名没有找到的异常
       * </p>
       *
       * @autor:zgg
       * @date:2022/3/17
       */
      public class UserNotFoundException extends ServiceException{
          public UserNotFoundException() {
          }
      
          public UserNotFoundException(String message) {
              super(message);
          }
      
          public UserNotFoundException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public UserNotFoundException(Throwable cause) {
              super(cause);
          }
      
          public UserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

2. 在UserService类中编写接口

   ```java
   /**
    * 用户登录方法
    * @param username 用户名
    * @param password 用户的密码
    * @return 当前匹配的用户数据，如果没有则返回null
    */
   User login(String username,String password);
   ```

3. 编写实现类UserServiceImpl

   > getMD5Password是自己定义的盐值加密方法，在注册的实现类中有写

   ```java
   @Override
   public User login(String username, String password) {
       User result = userMapper.findByUsername(username);
       if(result == null){
           throw new UserNotFoundException("用户不存在");
       }
       //检测用户的密码是否匹配
       //获取数据库中的加密后的密码
       String oldPassword = result.getPassword();
       //获取盐值，按照相同的md5算法规则进行加密
       String salt = result.getSalt();
       String newMd5Password = getMD5Password(password, salt);
       //将密码进行比较
       if(!newMd5Password.equals(oldPassword)){
           throw new PasswordNotMatchException("用户密码错误");
       }
       //判断is_delete字段的值是否为1，表示为被标记为删除
       if(result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
       //压缩数据的存储，只返回需要的数据给前端
       User user = new User();
       user.setUid(result.getUid());
       user.setUsername(result.getUsername());
       user.setAvatar(result.getAvatar());
       return user;
   }
   ```

4. 测试（这里的查询是查询通过盐值加密了的密码）

   ```java
   @Test
   public void login(){
       User zgg = userService.login("zgg", "123");
       System.out.println(zgg);
   }
   ```

#### 登录-控制层

1. 处理异常

   > 在基础上添加了两个判断语句

   ```java
   /**
    * <p>
    *控制层类的基类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/16
    */
   public class BaseController {
       //操作成功的状态码
       public static final int OK = 200;
   
       //请求处理方法，这个方法的返回值就是需要传递给前端的数据
       //自动将异常对象传递给此方法的参数列表上
       //当前项目中产生了异常，被统一拦截到此方法中，这个方法此时就充当的是请求处理方法，方法的返回值直接给前端
       @ExceptionHandler(ServiceException.class)//统一处理抛出的异常
       public JsonResult<Void> handleException(Throwable e){
           JsonResult<Void> result = new JsonResult<>(e);
           if(e instanceof UsernameDuplicatedException){
               result.setState(4000);
               result.setMessage("用户名已经被占用");
           }else if(e instanceof UserNotFoundException){
               result.setState(5001);
               result.setMessage("用户数据不存在的异常");
           }else if(e instanceof PasswordNotMatchException){
               result.setState(5002);
               result.setMessage("用户名的密码错误的异常");
           }else if(e instanceof InsertException){
               result.setState(5000);
               result.setMessage("注册时产生未知的异常");
           }
           return result;
       }
   
   }
   ```

2. 编写控制器UserController处理请求

   ```java
   @RequestMapping("/login")
   public JsonResult<User> login(String username,String password){
       User data = userService.login(username, password);
       return new JsonResult<User>(OK,data);
   
   }
   ```

3. 测试（http://localhost:8080/users/login?username=zgg&password=123）

   成功访问

#### 登录-前端页面

1. 在login.html页面依据前面所设置的请求来发送ajax请求

   from

   ```java
   <form id="form-login" action="index.html"  class="form-horizontal" role="form">
      <!--用户名-->
      <div class="form-group">
         <label for="username" class="col-md-3 control-label">名字：</label>
         <div class="col-md-8">
            <input name="username" type="text" class="form-control" id="username"  placeholder="请输入用户名">
         </div>
      </div>
      <!--密码-->
      <div class="form-group">
         <label for="password" class="col-md-3 control-label"> 密码：</label>
         <div class="col-md-8">
            <input name="password" type="text" class="form-control" id="password"  placeholder="请输入密码">
         </div>
      </div>
      <!-- 记住我-->
      <div class="form-group">
         <div class="col-md-offset-3 col-md-6">
            <div class="checkbox">
               <label>
                  <input type="checkbox" id="auto">自动登录
               </label>
            </div>
         </div>
      </div>
      <!--提交按钮-->
      <div class="form-group">
         <label class="col-md-3 control-label"></label>
         <div class="col-md-8">
            <input id="btn-login" class="btn btn-primary" type="button" value="登录" />
            <span class="pull-right"><small>还没有账号？</small><a href="register.html">注册</a></span>
         </div>
      </div>
   </form>
   ```

   ajax

   ```html
   <script type="text/javascript">
      $("#btn-login").click(function () {
         $.ajax({
            url:"/users/login",
            type:"POST",
            data:$("#form-login").serialize(),
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  alert("登陆成功");
                  //跳转到系统主页index.html
                  //相对路径来确定跳转的路径
                  location.href="./index.html";
               }else{
                  alert("登陆失败");
               }
            },
            error: function (xhr){
               alert("登录时产生未知的异常"+xhr.message);
            }
         });
      });
   </script>
   ```

2. 测试

#### session存储用户数据

1. 在父类BaseController中封装两个数据，获取uid和获取username的两个方法，用户头像暂时不考虑，将来封装cookie中来使用

   ```java
   /**
    * 获取session对象中的uid
    * @param session session对象
    * @return 当前登录的用户uid的值
    */
   protected final Integer getuidFromSession(HttpSession session){
       return Integer.valueOf(session.getAttribute("uid").toString());
   }
   
   /**
    * 获取当前登录用户的username
    * @param session session对象
    * @return 当前登录用户的用户名
    */
   protected final String getUsernameFromSession(HttpSession session){
       return session.getAttribute("username").toString();
   }
   ```

2. 在登录的控制器中将数据封装在session对象中，服务本身自动创建有session对象，已经是一个全局的session对象

   ```java
   @RequestMapping("/login")
   public JsonResult<User> login(String username, String password, HttpSession session){
       User data = userService.login(username, password);
       session.setAttribute("uid",data.getUid());
       session.setAttribute("username",data.getUsername());
       System.out.println(getuidFromSession(session));
       System.out.println(getUsernameFromSession(session));
       return new JsonResult<User>(OK,data);
   
   }
   ```

#### 拦截器

1. 在interceptor目录下创建LoginInterceptor类，编写拦截器

   ```java
   public class LoginInterceptor implements HandlerInterceptor {
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
           Object uid = request.getSession().getAttribute("uid");
           if(uid==null){
               response.sendRedirect("/web/login.html");
               return false;
           }
           return true;
   
       }
   }
   ```

2. 在config目录下注册拦截器，将其配置到spring容器中

   ```java
   /**
    * <p>
    *处理登录拦截器的注册
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/17
    */
   @Configuration//将这个类注册到spring容器中
   public class LoginInterceptorConfigurer implements WebMvcConfigurer {
       //将自定义的拦截器进行注册
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(new LoginInterceptor())
                   .addPathPatterns("/**")
                   .excludePathPatterns("/web/index.html","/web/login.html","/web/register.html"
                           ,"/web/product.html","/users/reg","/users/login","/index.html"
                           ,"/bootstrap3/**","/css/**","/images/**","/js/**");
       }
   }
   ```

### 修改密码

#### 修改密码-持久层

1. 规划需要执行的SQL语句

   1. 根据用户的uid修改用户的password值,同时需要修改修改人和组后一次修改时间

      ```sql
      update t_user set password=?,modified_user=?,modified_time=? where uid=?
      ```

   2. 根据uid查询用户的数据（判断用户是否在库中）。检测是否被标记为已删除，检测输入的原始密码是否正确

      ```sql
      select * from t_user where uid=?
      ```

2. 编写UserMapper

   ```java
   /**
    * 根据用户的uid来修改用户密码
    * @param uid 用户的uid
    * @param password 用户输入的新密码
    * @param modifiedUser 表示修改的执行者
    * @param modifiedTime 表示修改数据的时间
    * @return 返回值为受影响的行数
    */
   Integer updatePasswordByUid(@Param("uid") Integer uid,@Param("password") String password,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);
   
   /**
    * 根据uid查询用户的数据
    * @param uid 用户的id
    * @return 返回为查询到的数据
    */
   User findByUid(Integer uid);
   ```

3. 编写UserMapper.xml

   ```xml
   <update id="updatePasswordByUid">
       update t_user set
           password=#{password},modified_user=#{modifiedUser},modified_time=#{modifiedTime}
       where uid=#{uid};
   </update>
   <select id="findByUid" resultType="User">
       select * from t_user where uid=#{uid}
   </select>
   ```

4. 单元测试

   ```java
   @Test
   public void findByUsername(){
       User tim = userMapper.findByUsername("zgg");
       System.out.println(tim);
   }
   
   @Test
   public void updatePasswordByUid(){
       userMapper.updatePasswordByUid(2,"1234","管理员",new Date());
   
   }
   @Test
   public void findByUid(){
       User byUid = userMapper.findByUid(10);
       System.out.println(byUid);
   }
   ```

#### 修改密码-业务层

1. 规划异常

   1. 用户的原密码错误，is_delete==1、uid找不到，在用户没有发现的异常（之前已经写出来了）

   2. 在update的时候有可能产生未知的异常

      ```java
      @Test
      public void findByUsername(){
          User tim = userMapper.findByUsername("zgg");
          System.out.println(tim);
      }
      
      @Test
      public void updatePasswordByUid(){
          userMapper.updatePasswordByUid(2,"1234","管理员",new Date());
      
      }
      @Test
      public void findByUid(){
          User byUid = userMapper.findByUid(10);
          System.out.println(byUid);
      }
      ```

2. 编写UserService

   ```java
   /**
    * 用户更改密码方法
    * @param uid 用户的uid
    * @param username 用户名
    * @param oldPassword 用户的旧密码
    * @param newPassword 用户传过来的新密码
    */
   void changePassword(Integer uid,String username,String oldPassword,String newPassword);
   ```

3. 编写UserServiceImpl

   ```java
   @Override
   public void changePassword(Integer uid, String username, String oldPassword, String newPassword) {
       User result = userMapper.findByUid(uid);
       if(result==null||result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
       //输入的原始密码和数据库中的密码进行比较
       String oldMD5Password = getMD5Password(oldPassword, result.getSalt());
       if(!result.getPassword().equals(oldMD5Password)){
           throw new PasswordNotMatchException("密码错误");
       }
       //将新密码设置到数据库中
       String newMD5Password = getMD5Password(newPassword, result.getSalt());
       Integer rows = userMapper.updatePasswordByUid(uid, newMD5Password, username, new Date());
       if(rows!=1){
           throw new UpdateException("更新数据产生未知的异常");
       }
   }
   ```

4. 单元测试

   ```java
   @Test
   public void changePassword(){
       userService.changePassword(10,"zgg","1234","123");
   }
   ```

#### 修改密码-控制层

1. 处理异常

   > 在基础异常处理上添加了一个判断语句

   ```java
   else if(e instanceof UpdateException){
       result.setState(5001);
       result.setMessage("更新数据时产生未知的异常");
   }
   ```

2. 编写控制器处理请求

   ```java
   @RequestMapping("/change_password")
   public JsonResult<Void> changePassword(String oldPassword,String newPassword,HttpSession session){
       Integer uid = getuidFromSession(session);
       String username = getUsernameFromSession(session);
       userService.changePassword(uid,username,oldPassword,newPassword);
       return new JsonResult<Void>(OK);
   }
   ```

3. 测试

#### 修改密码-前端页面

1. 原始表单

   ```html
   <form id="form-change-password" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label">原密码：</label>
         <div class="col-md-8">
            <input name="oldPassword" type="text" class="form-control" placeholder="请输入原密码">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">新密码：</label>
         <div class="col-md-8">
            <input name="newPassword" type="text" class="form-control" placeholder="请输入新密码">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">确认密码：</label>
         <div class="col-md-8">
            <input type="text" class="form-control" placeholder="请再次输入新密码">
         </div>
      </div>
      <div class="form-group">
         <div class="col-sm-offset-2 col-sm-10">
            <input id="btn-change-password" type="button" class="btn btn-primary" value="修改" />
         </div>
      </div>
   </form>
   ```

2. 使用ajax

   ```html
   <script type="text/javascript">
      $("#btn-change-password").click(function () {
         $.ajax({
            url:"/users/change_password",
            type:"POST",
            data:$("#form-change-password").serialize(),
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  alert("密码修改成功");
                  location.href="./index.html";
               }else{
                  alert("密码修改失败");
               }
            },
            error: function (xhr){
               alert("修改密码时产生未知的异常"+xhr.message);
            }
         });
      });
   </script>
   ```

### 个人资料

#### 个人资料-持久层

1. 规划需要的SQL语句

   1. 更新用户信息的sql语句

      ```sql
      update t_user set phone=?,email=?,gender=?,modified_user=?,modified_time=? where uid=?
      ```

   2. 根据uid查询用户的数据(可以使用前面功能的语句)

2. 编写UserMapper

   ```java
   /**
    * 更新用户的数据信息
    * @param user  用户的数据
    * @return 返回值为受影响的行数
    */
   Integer updateInfoByUid(User user);
   ```

3. 编写UserMapper.xml

   ```xml
   <update id="updateInfoByUid" parameterType="User">
       update t_user set
           <if test="phone!=null and phone!=''">phone=#{phone},</if>
           <if test="email!=null and email!=''">email=#{email},</if>
           <if test="gender!=null and gender!=''">gender=#{gender},</if>
           modified_user=#{modifiedUser},
           modified_time=#{modifiedTime}
       where uid=${uid}
   </update>
   ```

4. 测试

   ```java
   @Test
   public void updateInfoByUid(){
       User user = new User();
       user.setUid(10);
       user.setPhone("123456789");
       user.setEmail("123456@qq.com");
       user.setGender(1);
       userMapper.updateInfoByUid(user);
   }
   ```

#### 个人资料-业务层

1. 规划异常

   1. 需要将用户信息显示在页面
   2. 需要判断数据更新时是否产生异常

2. 编写UserService

   ```java
   /**
   * 根据uid查询用户的数据
   * @param uid 用户的id
   * @return 返回为查询到的数据
   */
   User getByUid(Integer uid);
   /**
    *更新用户的数据操作
    * @param uid   uid用户的id
    * @param username  用户的名称
    * @param user  用户对象的数据
    */
   void changeInfo(Integer uid,String username,User user);
   ```

3. 编写UserServiceImpl

   ```java
   @Override
   public User getByUid(Integer uid) {
       User result = userMapper.findByUid(uid);
       if(result==null||result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
   
       User user = new User();
       user.setUsername(result.getUsername());
       user.setPhone(result.getPhone());
       user.setEmail(result.getEmail());
       user.setGender(result.getGender());
   
       return user;
   }
   
   @Override
   public void changeInfo(Integer uid, String username, User user) {
       User result = userMapper.findByUid(uid);
       if(result==null||result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
       user.setUid(uid);
       user.setModifiedUser(username);
       user.setModifiedTime(new Date());
       Integer rows = userMapper.updateInfoByUid(user);
       if(rows!=1){
           throw new UpdateException("更新数据时产生未知的异常");
       }
   
   }
   ```

4. 测试

   ```java
   @Test
   public void getByUid(){
       User byUid = userService.getByUid(10);
       System.out.println(byUid);
   }
   @Test
   public void changeInfo(){
       User user = new User();
       user.setPhone("1123456");
       user.setEmail("21221212@qq.com");
       user.setGender(0);
       userService.changeInfo(10,"zgg",user);
   }
   ```

#### 个人资料-控制层

1. 处理异常

2. 编写控制器getByUid

   > 用于前端页面显示数据

   ```java
   @RequestMapping("/get_by_uid")
   public JsonResult<User> getByUid(HttpSession session){
       User data = userService.getByUid(getuidFromSession(session));
       return new JsonResult<User>(OK,data);
   }
   ```

3. 测试

4. 编写控制器changeInfo

   ```java
   @RequestMapping("/change_info")
   public JsonResult<Void> changeInfo(User user,HttpSession session){
       //user对象有四部分的数据：username、phone,email,gender
       //uid数据需要再次封装到user对象中
       userService.changeInfo(getuidFromSession(session),getUsernameFromSession(session),user);
   
       return new JsonResult<Void>(OK);
   }
   ```

5. 测试（http://localhost:8080/users/change_info?phone=1125665867&email=87987654@qq.com&gender=1）


#### 个人资料-前端页面

1. 原始表单

   ```java
   <form id="form-change-info" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label">用户名：</label>
         <div class="col-md-8">
            <input id="username" type="text" class="form-control" readonly="readonly">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">电话号码：</label>
         <div class="col-md-8">
            <input id="phone" name="phone" type="text" class="form-control" placeholder="请输入电话号码">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">电子邮箱：</label>
         <div class="col-md-8">
            <input id="email" name="email" type="text" class="form-control" placeholder="请输入电子邮箱">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">性别：</label>
         <div class="col-md-8">
            <label class="radio-inline">
               <input id="gender-male" type="radio" name="gender" value="1">男
            </label>
            <label class="radio-inline">
               <input id="gender-female" type="radio" name="gender" value="0">女
            </label>
         </div>
      </div>
      <div class="form-group">
         <div class="col-sm-offset-2 col-sm-10">
            <input id="btn-change-info" type="button" class="btn btn-primary" value="修改" />
         </div>
      </div>
   </form>
   ```

2. ajax编写（两个要求需要实现）

   - 在打开页面时自动发送ajax请求，查询到的数据填充到这个页面

   - 检测用户点击了修改按钮后发送一个ajax请求

   ```html
   <script type="text/javascript">
   			$("#btn-change-info").click(function () {
   				$.ajax({
   					url:"/users/change_info",
   					type:"POST",
   					data:$("#form-change-info").serialize(),
   					dataType:"JSON",
   					success: function (json){
   						if (json.state == 200){
   							alert("用户信息修改成功");
   							//重新加载当前的页面
   							// location.href="./userdata.html";
   						}else{
   							alert("用户信息修改失败");
   						}
   					},
   					error: function (xhr){
   						alert("用户信息修改时产生异常"+xhr.message);
   					}
   				});
   			});
   			/**
   			 * 一旦检测到当前的页面被加载，就会出发ready方法
   			 * $(document).ready(function(){
   			 *     //编写业务代码
   			 * })
   			 */
   			$(document).ready(function(){
   				$.ajax({
   					url:"/users/get_by_uid",
   					type:"GET",
   					data:$("#form-change-info").serialize(),
   					dataType:"JSON",
   					success: function (json){
   						if (json.state == 200){
   							//将查询到的数据重新设置到控件中
   							$("#username").val(json.data.username);
   							$("#phone").val(json.data.phone);
   							$("#email").val(json.data.email);
   							$("#gender").val(json.data.gender);
   							let radio =json.data.gender == 0 ?$("#gender-female"):$("#gender-male");
   							//prop()表示给某个元素添加属性及属性的值
   							radio.prop("checked","checked")
   						}else{
   							alert("数据不存在");
   						}
   					},
   					error: function (xhr){
   						alert("查询用户信息产生未知的异常"+xhr.message);
   					}
   				});
   			});
   		</script>
   ```

### 上传头像

#### 上传头像-持久层

1. 规划sql语句

   1. 将对象文件保存在操作系统上，然后把这个文件路径记录在数据库内

      ```sql
      update t_user avatar=?,modifiec_user=?,userdified_time=? where uid=?
      ```

2. 编写UserMapper

   ```java
   /**
    * 上传头像路径
    * @param uid 用户的uid
    * @param avatar    用户上柴内动路径
    * @param modifiedUser  修改者
    * @param modifiedTime  修改时间
    * @return
    */
   Integer updateAvatarByUid(@Param("uid") Integer uid,@Param("avatar") String avatar,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);
   ```

3. 编写UserMapper.xml

   ```xml
   <update id="updateAvatarByUid">
       update t_user set
           avatar=#{avatar},
           modified_user=#{modifiedUser},
           modified_time=#{modifiedTime}
       where uid=#{uid}
   </update>
   ```

4. 编写单元测试

   ```java
   @Test
   public void updateAvatarByUid(){
       userMapper.updateAvatarByUid(10,"1324654","zgg",new Date());
   }
   ```

#### 上传头像-业务层

1. 规划异常

   1. 用户数据不存在
   2. 更新时产生异常

2. 编写UserService

   ```java
   /**
    * 跟新用户的头像
    * @param uid    用户的uid
    * @param avatar    头像的催促路径
    * @param username  用户的名称
    */
   void changeAvatar(Integer uid,String avatar,String username);
   ```

3. 编写UserServiceImpl

   ```java
   @Override
   public void changeAvatar(Integer uid, String avatar, String username) {
       User result = userMapper.findByUid(uid);
       if(result==null||result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
       Integer rows = userMapper.updateAvatarByUid(uid, avatar, username, new Date());
       if(rows!=1){
           throw new UpdateException("更新数据时产生未知的异常");
       }
   }
   ```

4. 编写单元测试

   ```java
   @Test
   public void changeAvatar(){
       userService.changeAvatar(10,"qwqreqwr","管理员");
   }
   ```

#### 上传头像-控制层

1. 规划异常

   - 文件异常的父类:

     FileUploadException 泛指文件上传的异常(父类)，继承RuntimeException

   - 父类是：FileUploadException 

     FileEmptyExcepetion 文件为空的异常

     FileSizeException 文件大小超出限制

     FileTypeException 文件类型异常

     FileUploadIOException 文件读写的异常

     FileStateException	文件状态异常

   在controller目录下新建ex目录存放异常

   FileUploadException 

   ```java
   /**
    * <p>
    *文件异常的父类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileUploadException extends RuntimeException{
       public FileUploadException() {
       }
   
       public FileUploadException(String message) {
           super(message);
       }
   
       public FileUploadException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileUploadException(Throwable cause) {
           super(cause);
       }
   
       public FileUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   FileEmptyExcepetion 

   ```java
   **
    * <p>
    *文件为空的异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileEmptyExcepetion extends FileUploadException{
       public FileEmptyExcepetion() {
       }
   
       public FileEmptyExcepetion(String message) {
           super(message);
       }
   
       public FileEmptyExcepetion(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileEmptyExcepetion(Throwable cause) {
           super(cause);
       }
   
       public FileEmptyExcepetion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   FileSizeException 

   ```java
   **
    * <p>
    *文件大小超出限制
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileSizeException extends FileUploadException{
       public FileSizeException() {
       }
   
       public FileSizeException(String message) {
           super(message);
       }
   
       public FileSizeException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileSizeException(Throwable cause) {
           super(cause);
       }
   
       public FileSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   FileTypeException

   ```java
   **
    * <p>
    *文件类型异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileTypeException extends FileUploadException{
       public FileTypeException() {
       }
   
       public FileTypeException(String message) {
           super(message);
       }
   
       public FileTypeException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileTypeException(Throwable cause) {
           super(cause);
       }
   
       public FileTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   FileUploadIOException

   ```java
   /**
    * <p>
    *文件读写的异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileUploadIOException extends FileUploadException{
       public FileUploadIOException() {
       }
   
       public FileUploadIOException(String message) {
           super(message);
       }
   
       public FileUploadIOException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileUploadIOException(Throwable cause) {
           super(cause);
       }
   
       public FileUploadIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

   FileStateException

   ```java
   /**
    * <p>
    *文件状态异常
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public class FileStateException extends FileUploadException{
       public FileStateException() {
       }
   
       public FileStateException(String message) {
           super(message);
       }
   
       public FileStateException(String message, Throwable cause) {
           super(message, cause);
       }
   
       public FileStateException(Throwable cause) {
           super(cause);
       }
   
       public FileStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
           super(message, cause, enableSuppression, writableStackTrace);
       }
   }
   ```

2. 处理异常，在基类BaseController中处理异常

   ```java
   else if(e instanceof FileEmptyExcepetion){
       result.setState(6000);
       result.setMessage("上传文件产生空异常");
   }else if(e instanceof FileSizeException){
       result.setState(6001);
       result.setMessage("上传文件大小异常");
   }else if(e instanceof FileTypeException){
       result.setState(6002);
       result.setMessage("上传文件类型异常");
   }else if(e instanceof FileStateException){
       result.setState(6003);
       result.setMessage("上传文件状态异常");
   }else if(e instanceof FileUploadIOException){
       result.setState(6004);
       result.setMessage("上传文件产生读写异常");
   }
   ```

   同时在异常统一处理方法的参数上增加新的异常做处理

   ```java
   @ExceptionHandler({ServiceException.class,FileUploadException.class})//统一处理抛出的异常
   ```

3. 编写控制器

   ```java
   //设置上传文件的最大值
   public static final int AVATAR_MAX_SIZE=10*1024*1024;
   //限制上传文件的类型
   public static final List<String> AVATAR_TYPE=new ArrayList<>();
   static {
       AVATAR_TYPE.add("image/jpeg");
       AVATAR_TYPE.add("image/png");
       AVATAR_TYPE.add("image/bmp");
       AVATAR_TYPE.add("image/gif");
   }
   
   /**
        * 修改用户的头像
        * MultipartFile接口是SpringMVC提供的一个接口，这个接口为我们
        * 包装了获取文件类型的数据（任何类型的file都可以接收），SpringBoot
        * 它有整合了SpringMVC，只需要在处理请求的方法参数列表上声明一个参数
        * 类型为MultipartFile的参数，然后SpringBoot自动将传递给服务的文件
        * 数据赋值给这个参数
        *
        * @RequestParam 表示请求中的参数，将请求中的参数注入请求处理方法的
        * 某个参数上，如果名称不一致则可以使用@RequestParam注解进行标记和映射
        *
        * @param session
        * @param file
        * @return
        */
   @RequestMapping("/change_avatar")
   public JsonResult<String> changeAvatar(
       HttpSession session,
       @RequestParam("file") MultipartFile file) throws FileNotFoundException {
       //判断文件是否为null
       if(file.isEmpty()){
           throw new FileEmptyExcepetion("文件为空");
       }
       //判断文件大小
       if(file.getSize()>AVATAR_MAX_SIZE){
           throw new FileSizeException("文件超出限制");
       }
       //判断文件类型
       String contentType = file.getContentType();
       //如果集合包含某个元素返回true，这里取反
       if(!AVATAR_TYPE.contains(contentType)){
           throw new FileTypeException("文件类型不支持");
       }
       //上传的文件.../upload/文件.png
       //        String parent=session.getServletContext().getRealPath("/upload");
       //        String parent= ResourceUtils.getURL("classpath:").getPath()+"static/upload/";
       String parent=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\images\\upload";
       System.out.println(parent);
       //File对象指向这个路径，File是否存在
       File dir = new File(parent);
       if(!dir.exists()){//检测目录是否存在
           dir.mkdirs();//创建当前目录
       }
       //获取到这个文件的名称，uuid工具类来生成一个新的字符串作为文件名
       //例如：avatao01.png
       String originalFilename = file.getOriginalFilename();
       int index = originalFilename.lastIndexOf(".");
       String suffix = originalFilename.substring(index);
       String s = UUID.randomUUID().toString().toUpperCase();
       String filename= s+suffix;
   
       //创建一个空文件
       File dest = new File(dir, filename);
       //参数file中数据写入到这个空文件中
       try {
           file.transferTo(dest);//将file文件中的数据写入到dest文件中
       } catch (IOException e) {
           throw new FileUploadIOException("文件读写异常");
       }catch (FileStateException e){
           throw new FileStateException("文件状态异常");
       }
       //返回头像的路径/upload/test.png
       String avatar="/static/images/upload/"+filename;
       System.out.println(avatar);
       userService.changeAvatar(getuidFromSession(session),avatar,getUsernameFromSession(session));
       //返回用户头像的路径给前端页面，将来用于头像展示使用
       return new JsonResult<String>(OK,avatar);
   }
   
   }
   ```

#### 上传头像-前端页面

##### 使用表单

1. 如果需要使用表单进行文件的上传，需要给表单显示的添加一个属性enctype="multipart/form-data"声明出来，不会讲目标文件的数据结构做修改再上传

   ```html
   <form action="/users/change_avatar" method="post" enctype="multipart/form-data" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label">选择头像:</label>
         <div class="col-md-5">
            <img id="img-avatar" src="../images/index/user.jpg" class="img-responsive" />
         </div>
         <div class="clearfix"></div>
         <div class="col-md-offset-2 col-md-4">
            <input type="file" name="file">
         </div>
      </div>
      <div class="form-group">
         <div class="col-sm-offset-2 col-sm-10">
            <input type="submit" class="btn btn-primary" value="上传" />
         </div>
      </div>
   </form>
   ```

2. 测试是否能够上传

##### 使用ajax

###### 显示图片

1. 原始表单

   ```html
   <form id="form-change-avatar" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label">选择头像:</label>
         <div class="col-md-5">
            <img id="img-avatar" src="../images/index/user.jpg" class="img-responsive" />
         </div>
         <div class="clearfix"></div>
         <div class="col-md-offset-2 col-md-4">
            <input type="file" name="file">
         </div>
      </div>
      <div class="form-group">
         <div class="col-sm-offset-2 col-sm-10">
            <input id="btn-change-avatar" type="button" class="btn btn-primary" value="上传" />
         </div>
      </div>
   </form>
   ```

2. 页面中通过ajax请求来提交文件

   ```html
   <script type="text/javascript">
      $("#btn-change-avatar").click(function () {
         $.ajax({
            url:"/users/change_avatar",
            type:"POST",
            //将表单中的数据保持原有的数据结构进行数据的提交
            //文件类型的数据可以使用FormData对象进行存储,[0]，指表单中的第0个控件
            data:new FormData($("#form-change-avatar")[0]),
            processData:false,//处理数据的形式，关闭磨处理数据，默认为串
            contentType:false,//提交数据的形式，关闭默认提交数据的形式，默认为串
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  alert("头像修改成功");
                  //将服务器段返回的头像地址设置到img标签的src属性
                  //attr(属性，属性值：给某个属性设置某个值)
                  $("#img-avatar").attr("src",json.data)
               }else{
                  alert("头像修改失败");
               }
            },
            error: function (xhr){
               alert("修改头像时产生未知的异常"+xhr.message);
            }
         });
      });
   </script>
   ```

###### 登陆后显示头像

> 使用cookie来存储头像

1. 前端使用需要导入cookie.js文件（需要放在js引入文件后）

   ```html
   <script src="../bootstrap3/js/jquery.cookie.js" type="text/javascript" charset="utf-8"></script>
   ```

2. 在login.html页面的js中调用cookie方法

   ```js
   //将服务器返回的头像路径设置到cookie中
   $.cookie("avatar",json.data.avatar,{expires: 7});
   ```

3. 在uploda.html中引入cookie.js文件(注意，要在放在js引入文件)

   ```html
   <script src="../bootstrap3/js/jquery.cookie.js" type="text/javascript" charset="utf-8"></script>
   ```

4. 在upload.html页面通过ready()自动读取cookie中的数据

   ```js
   $(document).ready(function(){
      let avatar=$.cookie("avatar");
      console.log(avatar);
      //将cookie值获取出来设置到头像的src属性上
      $("#img-avatar").attr("src",avatar);
   });
   ```

5. 设置更新完图片后更新cookie

   ```js
    //将服务器返回的头像路径设置到cookie中
   $.cookie("avatar",json.data,{expires: 7});
   ```

   

#### 解决bug

##### 更改默认的大小限制

1. SpringMVC默认为1MB文件可以进行上传，手动的去修改SpringMVC上传文件的大小

   方式一：直接在配置文件中进行配置

   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 10MB
         max-request-size: 15MB
   ```

   方式二：需要采用ava代码的形式来设置文件的上传大小的限制。主类中进行配置，可以定义一个方法,必须使用@Bean修饰来修饰。在类的前面添加@Configration注解进行修改类。 MutipartConfigElement类型

   ```java
   @Configuration//表示配置类
   @SpringBootApplication
   @MapperScan("com.zgg.store.mapper")
   public class StoreApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(StoreApplication.class, args);
       }
       @Bean
       public MultipartConfigElement getMultipartConfigElement(){
           //创建一个配置的工厂类对象
           MultipartConfigFactory factory = new MultipartConfigFactory();
           //设置需要创建的对象的相关信息
           factory.setMaxFileSize(DataSize.of(10, DataUnit.MEGABYTES));
           factory.setMaxRequestSize(DataSize.of(15,DataUnit.MEGABYTES));
   
           //通过工厂类来创建MultipartConfigElement对象
           return factory.createMultipartConfig();
       }
   }
   ```

### 新增收货地址

#### 新增收货地址-数据表创建

```sql
CREATE TABLE t_address (
	aid INT AUTO_INCREMENT COMMENT '收货地址id',
	uid INT COMMENT '归属的用户id',
	NAME VARCHAR(20) COMMENT '收货人姓名',
	province_name VARCHAR(15) COMMENT '省-名称',
	province_code CHAR(6) COMMENT '省-行政代号',
	city_name VARCHAR(15) COMMENT '市-名称',
	city_code CHAR(6) COMMENT '市-行政代号',
	area_name VARCHAR(15) COMMENT '区-名称',
	area_code CHAR(6) COMMENT '区-行政代号',
	zip CHAR(6) COMMENT '邮政编码',
	address VARCHAR(50) COMMENT '详细地址',
	phone VARCHAR(20) COMMENT '手机',
	tel VARCHAR(20) COMMENT '固话',
	tag VARCHAR(6) COMMENT '标签',
	is_default INT COMMENT '是否默认：0-不默认，1-默认',
	created_user VARCHAR(20) COMMENT '创建人',
	created_time DATETIME COMMENT '创建时间',
	modified_user VARCHAR(20) COMMENT '修改人',
	modified_time DATETIME COMMENT '修改时间',
	PRIMARY KEY (aid)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

#### 新增收货地址-实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address extends BaseEntity{
    private Integer aid;//收货地址id
    private Integer uid;//归属的用户id
    private String name;//收货人姓名
    private String provinceName;//省-名称
    private String provinceCode;//省-行政代号
    private String cityName;//市-名称
    private String cityCode;//市-行政代号
    private String areaName;//区-名称
    private String areaCode;//区-行政代号
    private String zip;//邮政编码
    private String address;//详细地址
    private String phone;//手机
    private String tel;//固话
    private String tag;//标签
    private Integer isDefault;//是否默认：0-不默认，1-默认
}
```

#### 新增收货地址-持久层

1. 规划需要执行的sql语句

   1. 添加收货地址

      ```insert
      insert into t_address (除了aid外字段) values (字段值列表)
      ```

   2. 一个用户的收货地址规定最多只能有20条数据对应，在插入用户数据之前先做查询

      ```sql
      select count(*) t_address where uid=?
      ```

2. 编写AddressMapper

   ```java
   public interface AddressMapper {
       /**
        * 插入用户的收货地址数据
        * @param address   收货地址数据
        * @return  受影响的行数
        */
       Integer insert(Address address);
   
       /**
        * 根据用户的id统计收货地址数量
        * @param uid 用户的id
        * @return  当前用户的收货地址总数
        */
       Integer countByUid(@Param("uid") Integer uid);
   
   }
   ```

3. 编写AddressMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.zgg.store.mapper.AddressMapper">
       <insert id="insert" parameterType="Address" useGeneratedKeys="true" keyProperty="aid">
           insert into t_address (uid,name,province_name,province_code,city_name,city_code,area_name,area_code,zip,address,phone,tel,tag,is_default,created_user,created_time,modified_user,modified_time)
               values (#{uid},#{name},#{provinceName},#{provinceCode},#{cityName},#{cityCode},#{areaName},#{areaCode},#{zip},#{address},#{phone},#{tel},#{tag},#{isDefault},#{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})
       </insert>
       <select id="countByUid" resultType="Integer">
           select count(*) from t_address where uid=#{uid}
       </select>
   </mapper>
   ```

4. 编写单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class AddressMapperTests {
       @Autowired
       AddressMapper addressMapper;
       @Test
       public void insert(){
           Address address = new Address();
           address.setUid(2);
           address.setName("ewrtertert");
           address.setPhone("5354asd435");
           addressMapper.insert(address);
       }
       @Test
       public void countByUid(){
   
           System.out.println(addressMapper.countByUid(2));
       }
   }
   ```

#### 新增收货地址-业务层

1. 规划异常

   1. 插入数据源时产生异常

   2. 当用户插入的地址是第一条时，需要将当前地址作为默认的收货地址

   3. 若查询到的结果大于20，抛出异常

      ```java
      /**
       * <p>
       *收货地址总数超出限制的异常（20条）
       * </p>
       *
       * @autor:zgg
       * @date:2022/3/19
       */
      public class AddressCountLimitException extends ServiceException{
          public AddressCountLimitException() {
          }
      
          public AddressCountLimitException(String message) {
              super(message);
          }
      
          public AddressCountLimitException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public AddressCountLimitException(Throwable cause) {
              super(cause);
          }
      
          public AddressCountLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

2. 编写AddressService

   ```java
   /**
    * <p>
    *收货地址业务接口
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   public interface AddressService {
       /**
        * 添加新的收货地址
        * @param uid   用户的id
        * @param username  用户的名称
        * @param address   表单传来的数据
        */
       void addNewAddress(Integer uid, String username, Address address);
   }
   ```

3. 编写application.yml配置文件，配置文件中写入拥有的最大地址数量，在AddressServiceImpl中进行读取

   ```yml
   user:
     address:
       max-count: 20
   ```

   读取方法示例：

   ```java
   //从yml配置文件中读取
   @Value("${user.address.max-count}")
   private Integer maxCount;
   ```

4. 编写AddressServiceImpl

   ```java
   /**
    * <p>
    *收货地址业务接口实现类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/19
    */
   @Service
   public class AddressServiceImpl implements AddressService {
       @Autowired
       AddressMapper addressMapper;
   
       //从yml配置文件中读取
       @Value("${user.address.max-count}")
       private Integer maxCount;
   
       @Override
       public void addNewAddress(Integer uid, String username, Address address) {
           //判断用户的收货地址是否达到上限
           Integer count = addressMapper.countByUid(uid);
           if (count>maxCount){
               throw new AddressCountLimitException("用户的收货地址达到上限");
           }
           //uid、isDefault
           address.setUid(uid);
           Integer isDefault = count == 0 ? 1 : 0;//1表示默认，0表示不是默认
           address.setIsDefault(isDefault);
           //补全4项日志
           address.setCreatedUser(username);
           address.setModifiedUser(username);
           address.setCreatedTime(new Date());
           address.setModifiedTime(new Date());
   
           Integer rows = addressMapper.insert(address);
           if(rows!=1){
               throw new InsertException("插入用户的收货地址产生未知异常");
           }
   
       }
   }
   ```

5. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class AddressServiceTests {
       @Autowired
       AddressService addressService;
   
       @Test
       public void addNewAddress(){
           Address address = new Address();
           address.setName("12345679");
           address.setPhone("987654321");
           addressService.addNewAddress(3,"管理员",address);
       }
   }
   ```

#### 新增收货地址-控制层

1. 处理异常

   ```java
   else if(e instanceof AddressCountLimitException){
       result.setState(4003);
       result.setMessage("用户的收货地址超出上限的异常");
   }
   ```

2. 编写控制器AddressController

   ```java
   @RestController
   @RequestMapping("/addresses")
   public class AddressController extends BaseController {
       @Autowired
       private AddressService addressService;
   
       @RequestMapping("/add_new_address")
       public JsonResult<Void> addNewAddress(Address address, HttpSession session){
           addressService.addNewAddress(getuidFromSession(session),getUsernameFromSession(session),address);
           return new JsonResult<>(OK);
       }
   }
   ```

3. 测试http://localhost:8080/addresses/add_new_address?name=tom&phone=12346



#### 新增收货地址-前端页面

1. 原始表单

   ```html
   <form id="form-add-new-address" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>收货人：</label>
         <div class="col-md-8">
            <input name="name" type="text" class="form-control" placeholder="请输入收货人姓名">
         </div>
      </div>
      <div class="address_content" data-toggle="distpicker">
         <div class="form-group">
            <label class="col-md-2 control-label"><span class="txtred">*</span>省/直辖市：</label>
            <div class="col-md-3">
               <select id="province-list" name="provinceCode" class="form-control" data-province="---- 选择省 ----"></select>
            </div>
            <label class="col-md-2 control-label"><span class="txtred">*</span>城市：</label>
            <div class="col-md-3">
               <select id="city-list" name="cityCode" class="form-control" data-city="---- 选择市 ----"></select>
            </div>
         </div>
         <div class="form-group">
            <label class="col-md-2 control-label"><span class="txtred">*</span>区县：</label>
            <div class="col-md-3">
               <select id="area-list" name="areaCode" class="form-control" data-district="---- 选择区 ----"></select>
            </div>
            <label class="col-md-2 control-label">邮政编码：</label>
            <div class="col-md-3">
               <input name="zip" type="text" class="form-control" placeholder="请输入邮政编码">
            </div>
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>详细地址：</label>
         <div class="col-md-8">
            <textarea name="address" class="form-control" rows="3" placeholder="输入详细的收货地址，小区名称、门牌号等"></textarea>
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>手机：</label>
         <div class="col-md-3">
            <input name="phone" type="text" class="form-control" placeholder="请输入手机号码">
         </div>
         <label class="col-md-2 control-label">固话：</label>
         <div class="col-md-3">
            <input name="tel" type="text" class="form-control" placeholder="请输入固定电话号码">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">地址类型：</label>
         <div class="col-md-8">
            <input name="tag" type="text" class="form-control" placeholder="请输入地址类型，如：家、公司或者学校">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><a href="address.html">返回</a>&nbsp;&nbsp;&nbsp;</label>
         <div class="col-sm-10">
            <input id="btn-add-new-address" type="button" class="col-md-1 btn btn-primary" value="保存" />
            <input type="reset" class="col-md-offset-1 col-md-1 btn btn-primary" value="重置" />
         </div>
      </div>
   </form>
   ```

2. 使用ajax实现保存

   ```html
   <script type="text/javascript">
      $("#btn-add-new-address").click(function () {
         $.ajax({
            url:"/addresses/add_new_address",
            type:"POST",
            data:$("#form-add-new-address").serialize(),
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  alert("新增收货地址保存成功");
               }else{
                  alert("新增收货地址保存失败");
               }
            },
            error: function (xhr){
               alert("新增收货地址时产生未知的异常"+xhr.message);
            }
         });
      });
   ```



### 获取省市区列表

#### 获取省市区列表-数据库

```sql
CREATE TABLE t_dict_district (
  id INT(11) NOT NULL AUTO_INCREMENT,
  parent VARCHAR(6) DEFAULT NULL,
  CODE VARCHAR(6) DEFAULT NULL,
  NAME VARCHAR(16) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

parent属性表示的是父区域代码号，省的父代码号+86

#### 获取省市区列表-实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class District extends BaseEntity{
    private Integer id;//省市区的id
    private String parent;//省市区的父代号
    private String code;//省市区的代号
    private String name;//省市区的名字
}
```

#### 获取省市区列表-持久层

1. 规划需要执行的sql语句

   1. 根据父代号进行查询

      ```sql
      select * from t_dict_district where parent=? order by code ASC
      ```

2. 创建DistrictMapper

   ```java
   public interface DistrictMapper {
       /**
        * 根据父代号查询区域信息
        * @param parent    父代号
        * @return  某个父区域下的所有区域列表
        */
       List<District> findByParent(String parent);
   }
   ```

3. 创建DistrictMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.zgg.store.mapper.DistrictMapper">
       <select id="findByParent" resultType="District">
           select * from t_dict_district where parent=#{parent} order by code ASC
       </select>
   
   </mapper>
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class DistrictMapperTests {
       @Autowired
       DistrictMapper districtMapper;
       @Test
       public void findByParent(){
           List<District> byParent = districtMapper.findByParent("210100");
           for (District district : byParent) {
               System.out.println(district);
           }
       }
   }
   ```

#### 获取省市区列表-业务层

1. 编写DistrictService

   ```java
   public interface DistrictService {
   
       /**
        * 根据父代号来查询区域信息（省市区）
        * @param parent    父代码
        * @return  多个区域的信息
        */
       List<District> getByParent(String parent);
   }
   ```

2. 编写DistrictServiceImpl

   ```java
    */
   @Service
   public class DistrictServiceImpl implements DistrictService {
       @Autowired
       private DistrictMapper districtMapper;
       @Override
       public List<District> getByParent(String parent) {
           List<District> list = districtMapper.findByParent(parent);
           /**
            * 在进行网络数据传输时，为了尽量避免无效数据的传递，可以将无效数据设置为null
            *可以节省流量，另一方面提升了效率
            */
           for (District district : list) {
               district.setId(null);
               district.setParent(null);
           }
           return list;
       }
   }
   ```

3. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class DistrictServiceTests {
       @Autowired
       DistrictService districtService;
   
       @Test
       public void addNewAddress(){
           //86表示钟哥，所有省的代号都是86
           List<District> byParent = districtService.getByParent("86");
           for (District district : byParent) {
               System.out.println(district);
           }
       }
   }
   ```

#### 获取省市区列表-控制层

1. 编写控制器DistrictController

   ```java
   @RestController
   @RequestMapping("/districts")
   public class DistrictController extends BaseController {
       @Autowired
       private DistrictService districtService;
   
       //districts开头的请求都被拦截到getByParent()方法
       @RequestMapping({"/",""})
       public JsonResult<List<District>> getByParent(String parent){
           List<District> data = districtService.getByParent(parent);
           return new JsonResult<>(OK,data);
       }
   }
   ```

2. 测试http://localhost:8080/districts?parent=86

   

#### 获取省市区列表-前端页面

1. 注释掉通过js来完成省市区列表加载的js代码

   ```html
   <!--       <script type="text/javascript" src="../js/distpicker.data.js"></script>-->
   <!--      <script type="text/javascript" src="../js/distpicker.js"></script>-->
   ```

2. 检查前端页面在提交省市区数据时是否有相关name属性和id属性

3. 运行前端看是否正常保存数据（除了省市区）

### 获取省市区的名称

#### 获取省市区的名称-持久层

1. 规划sql语句

   1. 根据当前code来获取当前省市区的名称，对应的查询语句

      ```sql
      select * from t_dist_district where code=?
      ```

2. 编写DistrictMapper

   ```java
   /**
    * 根据当前code来获取省市区的名称
    * @param code  当前code
    * @return  省市区的名称
    */
   String findNameByCode(String code);
   ```

3. 编写DistrictMapper.xml

   ```xml
   <select id="findNameByCode" resultType="String">
       select name from t_dict_district where code=#{code}
   </select>
   ```

4. 测试

   ```java
   @Test
   public void findNameByCode(){
       String nameByCode = districtMapper.findNameByCode("610000");
       System.out.println(nameByCode);
   }
   ```

#### 获取省市区的名称-业务层

1. 异常处理，没有异常需要处理

2. 编写DistrictService

   ```java
   String getNameByCode(String code);
   ```

3. 编写DistrictServiceImpl

   ```java
   @Override
   public String getNameByCode(String code) {
       String nameByCode = districtMapper.findNameByCode(code);
       return nameByCode;
   }
   ```

4. 测试

#### 获取省市区的名称-业务层优化

> 在AddressServiceImpl中

1. 提添加地址层，依赖于DistrictService层

   ```java
   @Autowired
   private DistrictService districtService;
   ```

2. 在addNewAddress方法中将districtService接口中获取到的省市区数据转移到address对象，这个对象中就包含了所有的用户收货地址的数据

   ```java
   //堆address对象中的数据进行补全：省市区
   String provinceName = districtService.getNameByCode(address.getProvinceCode());
   String cityName = districtService.getNameByCode(address.getCityCode());
   String areaName = districtService.getNameByCode(address.getAreaCode());
   address.setProvinceName(provinceName);
   address.setCityName(cityName);
   address.setAreaName(areaName);
   ```

#### 获取省市区的名称-前端页面

1. addAddress.html页面表单

   ```html
   <form id="form-add-new-address" class="form-horizontal" role="form">
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>收货人：</label>
         <div class="col-md-8">
            <input name="name" type="text" class="form-control" placeholder="请输入收货人姓名">
         </div>
      </div>
      <div class="address_content" data-toggle="distpicker">
         <div class="form-group">
            <label class="col-md-2 control-label"><span class="txtred">*</span>省/直辖市：</label>
            <div class="col-md-3">
               <select id="province-list" name="provinceCode" class="form-control" data-province="---- 选择省 ----"></select>
            </div>
            <label class="col-md-2 control-label"><span class="txtred">*</span>城市：</label>
            <div class="col-md-3">
               <select id="city-list" name="cityCode" class="form-control" data-city="---- 选择市 ----"></select>
            </div>
         </div>
         <div class="form-group">
            <label class="col-md-2 control-label"><span class="txtred">*</span>区县：</label>
            <div class="col-md-3">
               <select id="area-list" name="areaCode" class="form-control" data-district="---- 选择区 ----"></select>
            </div>
            <label class="col-md-2 control-label">邮政编码：</label>
            <div class="col-md-3">
               <input name="zip" type="text" class="form-control" placeholder="请输入邮政编码">
            </div>
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>详细地址：</label>
         <div class="col-md-8">
            <textarea name="address" class="form-control" rows="3" placeholder="输入详细的收货地址，小区名称、门牌号等"></textarea>
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><span class="txtred">*</span>手机：</label>
         <div class="col-md-3">
            <input name="phone" type="text" class="form-control" placeholder="请输入手机号码">
         </div>
         <label class="col-md-2 control-label">固话：</label>
         <div class="col-md-3">
            <input name="tel" type="text" class="form-control" placeholder="请输入固定电话号码">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label">地址类型：</label>
         <div class="col-md-8">
            <input name="tag" type="text" class="form-control" placeholder="请输入地址类型，如：家、公司或者学校">
         </div>
      </div>
      <div class="form-group">
         <label class="col-md-2 control-label"><a href="address.html">返回</a>&nbsp;&nbsp;&nbsp;</label>
         <div class="col-sm-10">
            <input id="btn-add-new-address" type="button" class="col-md-1 btn btn-primary" value="保存" />
            <input type="reset" class="col-md-offset-1 col-md-1 btn btn-primary" value="重置" />
         </div>
      </div>
   </form>
   ```

2. 编写相关事件代码

   ```html
   //value属性用于表示当前这个区域的code值
   let defaultOption="<option value='0'>---- 选择区 ----</option>"
   $(document).ready(function (){
      showProvinceList();
      //设置默认的"请选择"的值，作为控件的默认值
      $("#city-list").append(defaultOption);
      $("#area-list").append(defaultOption);
   });
   /**
    * change()函数用于监听某个控件是否发生改变，一旦发生改变，就会触发参数的函数
    * 需要传递一个dunction()
    */
   //市的下拉列表显示
   $("#province-list").change(function (){
      //获取到行政区父代码
      let parent=$("#province-list").val();
      //表示清空select下拉列表中的所有option元素
      $("#city-list").empty();
      $("#area-list").empty();
      //填充默认值 "请选择"
      $("#city-list").append(defaultOption)
      $("#area-list").append(defaultOption)
   
      if(parent==0){
         return;
      }
      $.ajax({
         url:"/districts",
         type:"get",
         data:"parent="+parent,
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
               let list = json.data;
               for (let i = 0; i < list.length; i++) {
                  let opt=
                        "<option value='"+list[i].code+"'>"+list[i].name+"</option>>";
                  $("#city-list").append(opt);
               }
            }else{
               alert("城市信息加载失败");
            }
         }
      });
   });
   //区的下拉列表显示
   $("#city-list").change(function (){
      //获取到行政区父代码
      let parent=$("#city-list").val();
      //表示清空select下拉列表中的所有option元素
      $("#area-list").empty();
      //填充默认值 "请选择"
      $("#area-list").append(defaultOption)
   
      if(parent==0){
         return;
      }
      $.ajax({
         url:"/districts",
         type:"get",
         data:"parent="+parent,
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
               let list = json.data;
               for (let i = 0; i < list.length; i++) {
                  let opt=
                        "<option value='"+list[i].code+"'>"+list[i].name+"</option>>";
                  $("#area-list").append(opt);
               }
            }else{
               alert("城市信息加载失败");
            }
         }
      });
   });
   
   //省的下拉列表数据展示
   function showProvinceList(){
      $.ajax({
         url:"/districts",
         type:"POST",
         data:"parent="+86,
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
               let list = json.data;
               for (let i = 0; i < list.length; i++) {
                  let opt=
                  "<option value='"+list[i].code+"'>"+list[i].name+"</option>>";
                  $("#province-list").append(opt);
               }
            }else{
               alert("省/直辖市信息加载失败");
            }
         }
      });
   }
   ```

### 收货地址列表展示

#### 收货地址列表展示-持久层

1. 规划sql语句

   1. 查询全部数据

      ```sql
      select * from t_address where uid=? order by is_default DESC,created_time DESC
      ```

2. 编写AddressMapper

3. 编写AddressMapper.xml

4. 单元测试

#### 收货地址列表展示-业务层

1. 规划异常（不需要抛出异常）

2. 编写AddressService

   ```java
   List<Address> getByUid(Integer uid);
   ```

3. 编写AddressServiceImpl

   ```java
   @Override
   public List<Address> getByUid(Integer uid) {
       List<Address> list = addressMapper.findByUid(uid);
       for (Address address : list) {
           //address.setAid(null);
           //address.setUid(null);
           address.setProvinceCode(null);
           address.setCityCode(null);
           address.setAreaCode(null);
           address.setTel(null);
           address.setIsDefault(null);
           address.setCreatedTime(null);
           address.setCreatedUser(null);
           address.setModifiedTime(null);
           address.setModifiedUser(null);
       }
       return list;
   }
   ```

4. 单元测试

   ```java
   @Test
   public void getByUid(){
       List<Address> byUid = addressService.getByUid(10);
       for (Address address : byUid) {
           System.out.println(address);
       }
   }
   ```

#### 收货地址列表展示-控制器

1. 编写控制器

   ```java
   @RequestMapping({"/",""})
   public JsonResult<List<Address>> getByUid(HttpSession session){
       List<Address> data = addressService.getByUid(getuidFromSession(session));
       return new JsonResult<>(OK,data);
   }
   ```

2. 测试

#### 收货地址列表展示-前端页面

1. 删除html页面中的静态数据

   ```html
   <tbody id="address-list">
   </tbody>
   ```

2. 编写ajax获取数据返回前端

   ```html
   <script type="text/javascript">
      $(document).ready(function (){
         showAddressList();
      });
   
      //展示用户收货地址数据列表
      function showAddressList(){
         $.ajax({
            url:"/addresses",
            type:"get",
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  let list = json.data;
                  for (let i = 0; i < list.length; i++) {
                     let tr='<tr>\n' +
                           '<td>#{tag}</td>\n' +
                           '<td>#{name}</td>\n' +
                           '<td>#{address}</td>\n' +
                           '<td>#{phone}</td>\n' +
                           '<td><a class="btn btn-xs btn-info"><span class="fa fa-edit"></span> 修改</a></td>\n' +
                           '<td><a class="btn btn-xs add-del btn-info"><span class="fa fa-trash-o"></span> 删除</a></td>\n' +
                           '<td><a class="btn btn-xs add-def btn-default">设为默认</a></td>\n' +
                           '</tr>';
                     tr = tr.replace(/#{tag}/g,list[i].tag);
                     tr = tr.replace(/#{name}/g,list[i].name);
                     tr = tr.replace(/#{phone}/g,list[i].phone);
                     tr = tr.replace(/#{address}/g,list[i].address);
                     $("#address-list").append(tr);
                  }
                  //将某个元素隐藏使用hide()方法
                   $(".add-def:eq(0)").hide();
               }else{
                  alert("用户收货地址数据加载失败");
               }
            }
         });
      }
   </script>
   ```

3. 测试

   

### 设置默认收货地址

#### 设置默认收货地址-持久层

1. 规划sql语句

   1. 检查当前用户想设置为默认收货地址的这条数据是否存在

      ```sql
      select * from t_address aid=?
      ```

   2. 在修改用户的收获默认地址之前，先将所有的收货地址设为非默认

      ```sql
      update t_address set is_default=0 where uid=?
      ```

   3. 将用户当前选中的这条记录设置为默认收货地址

      ```sql
      update t_address set is_default=1,modified_user=?   where aid=?
      ```

2. 编写AddressMapper

   ```java
   /**
    * 根据aid查询收货地址数据
    * @param aid   收货地址aid
    * @return  收货地址数据
    */
   Address findByAid(Integer aid);
   
   /**
    * 根据用户的uid值来修改用户的收货地址设置为非默认
    * @param uid   用户的id值
    * @return  受影响的行数
    */
   Integer UpdateNonDefault(Integer uid);
   
   
   /**
    *根据用户选中的记录将这条记录射为默认收货地址
    * @param aid
    * @param modifiedUser
    * @param modifiedTime
    * @return
    */
   Integer updateDefaultByAid(@Param("aid") Integer aid,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);
   ```

3. 编写AddressMapper.xml

   ```xml
   <select id="findByAid" resultType="Address">
       select * from t_address where aid=#{aid}
   </select>
   <update id="UpdateNonDefault">
       update t_address set is_default=0 where uid=#{uid}
   </update>
   <update id="updateDefaultByAid">
       update t_address
       set is_default=1,modified_user=#{modifiedUser},modified_time=#{modifiedTime}
       where aid=#{aid}
   </update>
   ```

4. 单元测试

   ```java
   @Test
   public void findByAid(){
   
       System.out.println(addressMapper.findByAid(6));
   }
   @Test
   public void UpdateNonDefault(){
   
       addressMapper.UpdateNonDefault(10);
   }
   @Test
   public void updateDefaultByAid(){
   
       addressMapper.updateDefaultByAid(6,"管理员",new Date());
   }
   ```

#### 设置默认收货地址-业务层

1. 规划异常

   1. 执行更新时产生异常

   2. 访问的数据不是当前登录用户的收货地址数据，非法访问:AccessDeniedException异常

      ```java
      public class AccessDeniedException extends ServiceException{
          public AccessDeniedException() {
          }
      
          public AccessDeniedException(String message) {
              super(message);
          }
      
          public AccessDeniedException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public AccessDeniedException(Throwable cause) {
              super(cause);
          }
      
          public AccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

   3. 收货地址有可能不存在的异常：AddressNotFountException

      ```java
      public class AddressNotFountException extends ServiceException{
          public AddressNotFountException() {
          }
      
          public AddressNotFountException(String message) {
              super(message);
          }
      
          public AddressNotFountException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public AddressNotFountException(Throwable cause) {
              super(cause);
          }
      
          public AddressNotFountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

2. 编写AddressService

   ```java
   /**
    * 修改某个用户的某条收货地址数据为默认收货地址
    * @param aid   收货地址的id
    * @param uid   用户的id
    * @param username  表示修改执行人
    */
   void setDefault(Integer aid,Integer uid,String username);
   ```

3. 编写AddressServiceImpl

   ```java
   @Override
   public void setDefault(Integer aid, Integer uid, String username) {
       Address result = addressMapper.findByAid(aid);
       if(result==null){
           throw new AddressNotFountException("收货地址不存在");
       }
       //判断当前获取到的收货地址数据的归属
       if(!uid.equals(result.getUid())){
           throw new AccessDeniedException("非法数据访问");
       }
       //将所有地址设置为非默认
       Integer rows = addressMapper.UpdateNonDefault(uid);
       if(rows<1){
           throw new UpdateException("更新数据时产生未知异常");
       }
       //将用户选中某条地址设置为默认收货地址
       Integer integer = addressMapper.updateDefaultByAid(aid, username, new Date());
       if(integer!=1){
           throw new UpdateException("更新数据时产生未知异常");
       }
   
   }
   ```

4. 单元测试

   ```java
   @Test
   public void setDefault(){
       addressService.setDefault(7,10,"管理员");
   }
   ```

#### 设置默认收货地址-控制层

1. 处理异常，BaseController中添加

   ```java
   else if(e instanceof AddressNotFountException){
       result.setState(4004);
       result.setMessage("用户的收货地址数据不存在的异常");
   }else if(e instanceof AccessDeniedException){
       result.setState(4005);
       result.setMessage("收货地址数据非法访问的异常");
   }
   ```

2. 编写控制器

   ```java
   @RequestMapping("/{aid}/set_default")
   public JsonResult<Void> setFault(@PathVariable("aid") Integer aid, HttpSession session){
       addressService.setDefault(aid,getuidFromSession(session),getUsernameFromSession(session));
       return new JsonResult<>(OK);
   }
   ```

3. 测试http://localhost:8080/addresses/6/set_default

   

#### 设置默认收货地址-前端页面

1. 在tr中添加函数点击事件

   ```html
   <td><a onclick="setDefault(#{aid})" class="btn btn-xs add-def btn-default">设为默认</a></td>
   ```

2. 更改占位符#{aid}的值

   ```html
   tr = tr.replace(/#{aid}/g,list[i].aid);
   ```

3. 编写点击事件函数

   ```html
   function setDefault(aid){
      $.ajax({
         url:"/addresses/"+aid+"/set_default",
         type:"POST",
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
   			//清空列表内容
   			$("#address-list").empty();
   			//重新展示列表
   			showAddressList();
            }else{
               alert("设置默认收货地址失败");
            }
         },
         error: function (xhr){
            alert("设置默认收货地址时产生未知的异常"+xhr.message);
         }
      });
   }
   ```

### 删除收货地址

#### 删除收货地址-持久层

1. 规划sql语句

   1. 在删除之前判断该数据是否存在，判断该条地址数据的归属是否是当前的用户

   2. 执行删除对应收货地址的信息

      ```sql
      delete from t_address where aid=?
      ```

   3. 如果用户删除的是默认收货的地址，将剩下的地址中的某一条设置为默认的收货地址，最新修改的收回地址设置为默认的收货地址

      ```sql
      select * from t_address where uid=? order by modified_time DESC LIMT 0,1
      ```

   4. 如果用户本身就只有一条收货地址的数据，删除后其它操作就可以不进行了

2. 编写AddressMapper

   ```java
   /**
    * 根据收货地址id删除收货地址数据
    * @param aid   收货地址的id
    * @return  受影响的行数
    */
   Integer deleteByAid(Integer aid);
   
   /**
    * 根据用户uid查询当前用户最后一次被修改的收货地址数据
    * @param uid   用户id
    * @return 收货地址数据
    */
   Address findLastModified(Integer uid);
   ```

3. 编写AddressMapper.xml

   ```xml
   <delete id="deleteByAid">
       delete from t_address where aid=#{aid}
   </delete>
   <select id="findLastModified" resultType="Address">
       select * from t_address where uid=#{uid} order by modified_time desc limit 0,1
   </select>
   ```

4. 测试

   ```java
   @Test
   public void deleteByAid(){
       addressMapper.deleteByAid(4);
   }
   @Test
   public void findLastModified(){
       Address lastModified = addressMapper.findLastModified(10);
       System.out.println(lastModified);
   }
   ```

#### 删除收货地址-业务层

1. 规划异常

   1. 执行删除时可能导致数据不能成功删除，抛出DeleteException异常

      ```java
      /**
       * <p>
       *
       * </p>
       *删除数据时产生的异常
       * @autor:zgg
       * @date:2022/3/21
       */
      public class DeleteException extends ServiceException{
          public DeleteException() {
          }
      
          public DeleteException(String message) {
              super(message);
          }
      
          public DeleteException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public DeleteException(Throwable cause) {
              super(cause);
          }
      
          public DeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

2. 编写AddressService

   ```java
   /**
    * 删除用户选中的收货地址数据
    * @param aid   收货地址id
    * @param uid   用户id
    * @param username  用户名
    */
   void delete(Integer aid,Integer uid,String username);
   ```

3. 编写AddressServiceImpl

   ```java
   @Override
   public void delete(Integer aid, Integer uid, String username) {
       Address result = addressMapper.findByAid(aid);
       if(result==null){
           throw new AddressNotFountException("收货地址不存在");
       }
       //判断当前获取到的收货地址数据的归属
       if(!uid.equals(result.getUid())){
           throw new AccessDeniedException("非法数据访问");
       }
       Integer rows = addressMapper.deleteByAid(aid);
       if(rows!=1){
           throw new DeleteException("删除数据产生异常");
       }
       Integer count = addressMapper.countByUid(uid);
       if(count==0){
           return;
       }
       Address address = addressMapper.findLastModified(uid);
       if(result.getIsDefault()==1){
           Integer row = addressMapper.updateDefaultByAid(address.getAid(), username, new Date());
           if(row!=1){
               throw new UpdateException("更新数据时产生未知异常");
           }
       }
   }
   ```

4. 测试

   ```java
   @Test
   public void delete(){
       addressService.delete(5,10,"管理员");
   }
   ```

#### 删除收货地址-控制层

1. 处理异常，BaseController中

   ```java
   else if(e instanceof DeleteException){
       result.setState(5002);
       result.setMessage("删除数据时产生未知的异常");
   }
   ```

2. 编写控制器

   ```java
   @RequestMapping("/{aid}/delete")
   public JsonResult<Void> delet(@PathVariable("aid") Integer aid,HttpSession session){
       addressService.delete(aid,getuidFromSession(session),getUsernameFromSession(session));
       return new JsonResult<>(OK);
   }
   ```

3. 测试

#### 删除收货地址-前端页面

1. 添加删除按钮的事件

   ```html
   <td><a onclick="deleteByAid(#{aid})" class="btn btn-xs add-del btn-info"><span class="fa fa-trash-o"></span> 删除</a></td>
   ```

2. 编写相关函数

   ```javascript
   function deleteByAid(aid){
      $.ajax({
         url:"/addresses/"+aid+"/delete",
         type:"POST",
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
               //清空列表内容
               $("#address-list").empty();
               //重新展示列表
               showAddressList();
               // location.href="./address.html";
            }else{
               alert("删除收货地址失败");
            }
         },
         error: function (xhr){
            alert("删除收货地址时产生未知的异常"+xhr.message);
         }
      });
   }
   ```

3. 测试

### 商品热销排行

#### 商品-创建数据库

```sql

CREATE TABLE t_product (
  id INT(20) NOT NULL COMMENT '商品id',
  category_id INT(20) DEFAULT NULL COMMENT '分类id',
  item_type VARCHAR(100) DEFAULT NULL COMMENT '商品系列',
  title VARCHAR(100) DEFAULT NULL COMMENT '商品标题',
  sell_point VARCHAR(150) DEFAULT NULL COMMENT '商品卖点',
  price BIGINT(20) DEFAULT NULL COMMENT '商品单价',
  num INT(10) DEFAULT NULL COMMENT '库存数量',
  image VARCHAR(500) DEFAULT NULL COMMENT '图片路径',
  STATUS INT(1) DEFAULT '1' COMMENT '商品状态  1：上架   2：下架   3：删除',
  priority INT(10) DEFAULT NULL COMMENT '显示优先级',
  created_time DATETIME DEFAULT NULL COMMENT '创建时间',
  modified_time DATETIME DEFAULT NULL COMMENT '最后修改时间',
  created_user VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  modified_user VARCHAR(50) DEFAULT NULL COMMENT '最后修改人',
  PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

#### 商品-实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {
    private Integer id;//商品id
    private Integer categoryId;//分类id
    private String itemType;//商品系列
    private String title;//商品标题
    private String sellPoint;//商品卖点
    private Long price;//商品单价
    private Integer num;//库存数量
    private String image;//图片路径
    private Integer status;//商品状态  1：上架   2：下架   3：删除
    private Integer priority;//显示优先级
}
```

#### 商品-持久层

1. 规划sql语句

   1. 查询热销商品列表的sql语句

      ```sql
      select * from t_product where status=1 order by priority desc limit 0,4
      ```

2. 编写ProduceMapper

   ```java
   public interface ProductMapper {
       List<Product>findHotList();
   }
   ```

3. 编写ProduceMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.zgg.store.mapper.ProductMapper">
       <select id="findHotList" resultType="Product">
           select * from t_product where status=1 order by priority desc limit 0,4
       </select>
   </mapper>
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class ProductMapperTests {
       @Autowired
       ProductMapper productMapper;
       @Test
       public void findHotList(){
           List<Product> hotList = productMapper.findHotList();
           for (Product product : hotList) {
               System.out.println(product);
           }
       }
   }
   ```

#### 商品-业务层

1. 编写ProductService

   ```java
   public interface ProductService {
       List<Product> findHotList();
   }
   ```

2. 编写ProductServiceImpl

   ```java
   @Service
   public class ProductServiceImpl implements ProductService {
       @Autowired
       private ProductMapper productMapper;
       @Override
       public List<Product> findHotList() {
           List<Product> hotList = productMapper.findHotList();
           for (Product product : hotList) {
               product.setPriority(null);
               product.setCreatedUser(null);
               product.setCreatedTime(null);
               product.setModifiedUser(null);
               product.setModifiedTime(null);
           }
           return hotList;
       }
   }
   ```

#### 商品-控制层

1. 编写控制器

   ```java
   @RestController
   @RequestMapping("/products")
   public class ProductController extends BaseController {
       @Autowired
       private ProductService productService;
   
       @RequestMapping("/host_list")
       public JsonResult<List<Product>> findHotList(){
           List<Product> data = productService.findHotList();
           return new JsonResult<>(OK,data);
       }
   }
   ```

2. 将路径加入到拦截器中放行

   ```java
   "/products/**"
   ```

3. 测试http://localhost:8080/products/host_list

   

#### 商品-前端页面

1. 修改原列表显示，删除需要重复的语句

   ```java
   <div id="hot-list" class="panel-body panel-item">
   
   </div>
   ```

2. 编写函数

   ```js
   <script type="text/javascript">
      $(document).ready(function (){
         showHotList();
      });
   
      //展示用户收货地址数据列表
      function showHotList(){
         $.ajax({
            url:"/products/host_list",
            type:"get",
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  let list = json.data;
                  for (let i = 0; i < list.length; i++) {
                     let tr='<div class="col-md-12">\n' +
                           '<div class="col-md-7 text-row-2"><a href="product.html?id=#{id}">#{title}</a></div>\n' +
                           '<div class="col-md-2">¥#{price}</div>\n' +
                           '<div class="col-md-3"><img src="..#{image}collect.png" class="img-responsive" /></div>\n' +
                           '</div>';
                     tr = tr.replace(/#{id}/g,list[i].id);
                     tr = tr.replace(/#{title}/g,list[i].title);
                     tr = tr.replace(/#{price}/g,list[i].price);
                     tr = tr.replace(/#{image}/g,list[i].image);
   
                     $("#hot-list").append(tr);
                  }
               }else{
                  alert("热销排行榜加载失败");
               }
            }
         });
      }
   </script>
   ```

3. 测试

### 显示的商品详情

#### 显示的商品详情-持久层

1. 编写ProductMapper

   ```java
   Product findById(Integer id);
   ```

2. 编写ProductMapper.xml

   ```xml
   <select id="findById" resultType="Product">
       select * from t_product where id=#{id}
   </select>
   ```

#### 显示的商品详情-业务层

1. 规划异常

   1. 找不到该商品ProductNotFoundException

      ```java
      public class ProductNotFoundException extends ServiceException{
          public ProductNotFoundException() {
          }
      
          public ProductNotFoundException(String message) {
              super(message);
          }
      
          public ProductNotFoundException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public ProductNotFoundException(Throwable cause) {
              super(cause);
          }
      
          public ProductNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

      

2. 编写ProductService

   ```java
   Product findById(Integer id);
   ```

3. 编写ProductServiceImpl

   ```java
   @Override
   public Product findById(Integer id) {
       Product product = productMapper.findById(id);
       if (product==null){
           throw new ProductNotFoundException("尝试访问的商品数据不存在");
       }
       product.setPriority(null);
       product.setCreatedUser(null);
       product.setCreatedTime(null);
       product.setModifiedUser(null);
       product.setModifiedTime(null);
       return product;
   }
   ```

#### 显示的商品详情-控制层

1. 处理异常

   ```java
   else if(e instanceof ProductNotFoundException){
       result.setState(4006);
       result.setMessage("商品数据不存在的异常");
   }
   ```

2. 编写控制器

   ```java
   @GetMapping("/{id}/details")
   public JsonResult<Product> getById(@PathVariable("id") Integer id){
       Product data = productService.findById(id);
       return new JsonResult<>(OK,data);
   }
   ```

3. 测试

#### 显示的商品详情-前端页面

> product.html

1. 解析通过url传过来的id，检查页面body标签内部的最后是否引入此文件，如果引入则无需重复引入

   ```html
   <script type="text/javascript" src="../js/jquery-getUrlParam.js"></script>
   ```

2. 编写相关js代码

   ```js
   <script type="text/javascript">
      //获取url中传过来的值
      let id=$.getUrlParam("id");
      alert(id);
      $(document).ready(function (){
         $.ajax({
            url:"/products/"+id+"/details",
            type:"get",
            dataType:"JSON",
            success: function (json){
               if (json.state == 200){
                  //html(),将()中的值传入div标签的内容中
                  $("#product-title").html(json.data.title);
                  $("#product-sell-point").html(json.data.sellPoint);
                  $("#product-price").html(json.data.price);
   
                  for (let i = 1; i <= 5; i++) {
                     $("product-image-"+i+"-big").attr("src",".."+json.data.image+i+"_big.png");
                     $("product-image-"+i).attr("src",".."+json.data.image+i);
                  }
               }else if (json.state==4006){//商品数据不存在的异常
                  location.href="index.html";
               }else{
                  alert("获取商品信息失败！"+json.message);
               }
            }
         });
      });
   </script>
   ```

### 加入购物车

#### 加入购物车-数据库创建

```sql
CREATE TABLE t_cart (
	cid INT AUTO_INCREMENT COMMENT '购物车数据id',
	uid INT NOT NULL COMMENT '用户id',
	pid INT NOT NULL COMMENT '商品id',
	price BIGINT COMMENT '加入时商品单价',
	num INT COMMENT '商品数量',
	created_user VARCHAR(20) COMMENT '创建人',
	created_time DATETIME COMMENT '创建时间',
	modified_user VARCHAR(20) COMMENT '修改人',
	modified_time DATETIME COMMENT '修改时间',
	PRIMARY KEY (cid)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

#### 加入购物车-实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends BaseEntity implements Serializable {
    private Integer cid;//购物车数据id
    private Integer uid;//用户id
    private Integer pid;//商品id
    private Long price;//加入时商品单价
    private Integer num;//商品数量
}
```

#### 加入购物车-持久层

1. 规划sql语句

   1. 想购物车表中插入数据

      ```sql
      insert into t_cart (aid除外) values (aid除外)
      ```

   2. 当当前的商品已经在购物车中存在，则直接更新num的数量即可

      ```sql
      update t_cart set num=? where cid=?
      ```

   3. 插入或者更新具体执行哪个语句，取决于数据库中是否有当前的这个购物车商品的数据

      ```sql
      select * from t_cart where pid=? and uid=?
      ```

2. 编写CartMapper

   ```java
   public interface CartMapper {
       /**
        * 插入购物车数据
        * @param cart  购物车数据
        * @return  受影响的行数
        */
       Integer insert(Cart cart);
   
       /**
        * 更新购物车某件商品的数量
        * @param cid   购物车数据id
        * @param num   更新的数量
        * @param modifiedUser  修改者
        * @param modifiedTime  修改时间
        * @return  受影响的行数
        */
       Integer updateNumByCid(Integer cid, Integer num, String modifiedUser, Date modifiedTime);
   
       /**
        * 根据用户的id和商品的id来查询购物车中的数据
        * @param uid   用户id
        * @param pid   商品id
        * @return  购物车中的数据
        */
       Cart findByUidAndPid(Integer uid,Integer pid);
   }
   ```

3. 编写CartMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.zgg.store.mapper.CartMapper">
       <insert id="insert" useGeneratedKeys="true" keyProperty="cid">
           insert into t_cart (uid,pid,price,num,created_user,created_time,modified_user,modified_time)
           values (#{uid},#{pid},#{price},#{num},#{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})
       </insert>
       <update id="updateNumByCid">
           update t_cart set
               num=#{num},
               modified_user=#{modifiedUser},
               modified_time=#{modifiedTime}
           where cid=#{cid}
       </update>
       <select id="findByUidAndPid" resultType="Cart">
           select * from t_cart where pid=#{pid} and uid=#{uid}
       </select>
   
   </mapper>
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class CartMapperTests {
       @Autowired
       CartMapper cartMapper;
       @Test
       public void insert(){
           Cart cart = new Cart();
           cart.setUid(10);
           cart.setPid(10000011);
           cart.setPrice(10l);
           cart.setNum(2);
           cartMapper.insert(cart);
       }
       @Test
       public void updateNumByCid(){
           cartMapper.updateNumByCid(1,4,"管理员",new Date());
       }
       @Test
       public void findByUidAndPid(){
           Cart byUidAndPid = cartMapper.findByUidAndPid(10, 10000011);
           System.out.println(byUidAndPid);
       }
   }
   ```

#### 加入购物车-业务层

1. 规划异常

   1. 插入数据时可能产生异常InsertException
   2. 更新数据时可能产生异常UpdateException

2. 编写CartService

   ```java
   public interface CartService {
       /**
        * 将商品添加到购物车中
        * @param uid 用户id
        * @param pid   商品id
        * @param amount    新增数量
        * @param username  用户名
        */
       void addToCart(Integer uid,Integer pid,Integer amount,String username);
   }
   ```

3. 编写CartServiceImpl

   ```java
   @Service
   public class CartServiceImpl implements CartService {
       @Autowired
       CartMapper cartMapper;
       @Autowired
       ProductMapper productMapper;
       @Override
       public void addToCart(Integer uid, Integer pid, Integer amount, String username) {
           //查询当前要添加的这个商品是否已存在于购物车中
           Cart result = cartMapper.findByUidAndPid(uid, pid);
           Date date = new Date();//保证时间一致
           //如果存在，则更新购物车中的num
           if(result!=null){
               Integer rows = cartMapper.updateNumByCid(result.getCid(), result.getNum()+amount, username, date);
               if(rows!=1){
                   throw new UpdateException("更新数据时出现未知的异常");
               }
           }else {//如果不存在，则插入商品
               Cart cart = new Cart();
               cart.setPid(pid);
               cart.setNum(amount);
               cart.setUid(uid);
               cart.setPrice(productMapper.findById(pid).getPrice());//价格直接从商品表中获取
               cart.setCreatedTime(date);
               cart.setCreatedUser(username);
               cart.setModifiedTime(date);
               cart.setModifiedUser(username);
               Integer insert = cartMapper.insert(cart);
               if(insert !=1){
                   throw new InsertException("插入数据时产生未知的异常");
               }
           }
       }
   }
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class CartServiceTests {
       @Autowired
       CartService cartService;
   
       @Test
       public void addToCart(){
           cartService.addToCart(10,10000003,2,"管理员");
       }
   }
   ```

#### 加入购物车-控制层

1. 没有需要处理的异常

2. 编写控制器

   ```java
   @RestController
   @RequestMapping("/carts")
   public class CartController extends BaseController{
       @Autowired
       private CartService cartService;
   
       @RequestMapping("/add_to_cart")
       public JsonResult<Void> addToCart(Integer pid, Integer amount, HttpSession session){
           cartService.addToCart(getuidFromSession(session),pid,amount,getUsernameFromSession(session));
           return new JsonResult<>(OK);
       }
   }
   ```

3. 测试http://localhost:8080/carts/add_to_cart?pid=10000021&amount=5

   

#### 加入购物车-前端页面

1. 在product.html页面给加入购物车按钮添加点击事件，并发送ajax请求

   ```java
   $("#btn-add-to-cart").click(function (){
      $.ajax({
         url:"/carts/add_to_cart",
         type:"POST",
         data: {
             //页面之前已经获取url中传过来的商品id值
   		  //let id=$.getUrlParam("id");
            "pid": id,
            "amount": $("#num").val()
         },
         dataType:"JSON",
         success: function (json){
            if (json.state == 200){
               alert("加入购物车成功");
               location.href="./index.html";
            }else{
               alert("加入购物车失败");
            }
         },
         error: function (xhr){
            alert("加入购物车时产生未知的异常"+xhr.message);
         }
      });
   });
   ```

2. 在ajax函数中data参数的数据设置的方式:

   - data: $("form表单选择).serialize)。当参数过多并且在同-一个表单中，字符串的提交等

   - data: new FormData( $("form表单选择)[0])。只适用提交文件

   - data: "username=Tom".适合参数值固定并且参数值列表有限，可以进行手动拼接

     ```text
     let user="Tom"
     data: "username="+user
     ```

   - 适用JSON格式提交数据

     ```text
     data:{
     	"username": "Tom",
     	"age": 18,
     	"sex": 0
     }
     ```

### 显示购物车列表

#### 显示购物车列表-持久层

1. 规划sql语句

   1. 关联查询用户的购物车数据（t_cart和t_product），查询时需要查询到商品的最新价格realprice，以便于更新

      ```sql
      # 多表查询如果字段不重复则不需要显示声明字段属于哪张表
      select cid,uid,pid,t_cart.price,t_cart.num,t_product.title,t_product.image,t_product.price as realprice
      from t_cart left join t_product on t_cart.pid=t_product.id
      where uid=#{uid}
      order by t_cart.createdTime desc
      ```

      VO：Value Object，值对象。当进行select查询时，查阅的结果数据多张表中的内容，此时发现结果集表内直接使用某个pojo实体类来接收，pojo实体类不能包含多表查询出来的结果。解决方式是：重新去构建应该新的对象，这个对象用于存储所查询出来的结果集对应的映射，所以把这个的对象称之为值对象

2. 编写vo类CartVo（在store目录下新建vo包，新建CartVo类）

   ```java
   /**
    * <p>
    *购物车数据的vo类
    * </p>
    *
    * @autor:zgg
    * @date:2022/3/22
    */
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class CartVo implements Serializable {
       private Integer cid;//购物车数据id
       private Integer uid;//用户id
       private Integer pid;//商品id
       private Long price;//加入时商品单价
       private Integer num;//商品数量
       private String title;//标题
       private String image;//图片
       private Long realPrice;//最新价格
   
   }
   ```

3. 编写CartMapper

   ```java
   /**
    * 查询用户购物车中的数据
    * @param uid   用户id
    * @return  用户的购物车数据
    */
   List<CartVo> findVOByUid(Integer uid);
   ```

4. 编写CartMapper.xml

   ```xml
   <select id="findVOByUid" resultType="com.zgg.store.vo.CartVo">
       select cid,uid,pid,t_cart.price,t_cart.num,t_product.title,t_product.image,t_product.price as realprice
       from t_cart left join t_product on t_cart.pid=t_product.id
       where uid=#{uid}
       order by t_cart.created_time desc
   </select>
   ```

5. 单元测试

   ```java
   @Test
   public void findVOByUid(){
       List<CartVo> voByUid = cartMapper.findVOByUid(10);
       for (CartVo cartVo : voByUid) {
           System.out.println(cartVo);
       }
   }
   ```

#### 显示购物车列表-业务层

1. 规划异常，查询不需要规划

2. 编写CartService

   ```java
   List<CartVo> getVOByUid(Integer uid);
   ```

3. 编写CartServiceImpl

   ```java
   @Override
   public List<CartVo> getVOByUid(Integer uid) {
       return cartMapper.findVOByUid(uid);
   }
   ```

#### 显示购物车列表-控制层

1. 处理异常，无需要新增的异常

2. 编写控制器

   ```java
   @RequestMapping({"","/"})
   public JsonResult<List<CartVo>> getVOByUid(HttpSession session){
       List<CartVo> data = cartService.getVOByUid(getuidFromSession(session));
       return new JsonResult<>(OK,data);
   }
   ```

3. 测试http://localhost:8080/carts

   

#### 显示购物车列表-前端页面

> cart.html

1. 注释掉cart.js文件

   ```html
   <!--       <script src="../js/cart.js" type="text/javascript" charset="utf-8"></script>-->
   ```

2. 修改结算按钮，修改为type="button"

   ```html
   <input type="button" value="  结  算  " class="btn btn-primary btn-lg link-account" />
   ```

3. 找到tbody标签

   ```html
   <tbody id="cart-list" class="cart-body">
      <tr>
         <td>
            <input type="checkbox" class="ckitem" />
         </td>
         <td><img src="../images/portal/12DELLXPS13-silvery/collect.png" class="img-responsive" /></td>
         <td>联想(Lenovo)小新Air13 Pro 13.3英寸14.8mm超轻薄笔记本电脑</td>
         <td>¥<span id="goodsPrice1">5298</span></td>
         <td>
            <input type="button" value="-" class="num-btn" onclick="reduceNum(1)" />
            <input id="goodsCount1" type="text" size="2" readonly="readonly" class="num-text" value="1">
            <input class="num-btn" type="button" value="+" onclick="addNum(1)" />
         </td>
         <td><span id="goodsCast1"></span></td>
         <td>
            <input type="button" onclick="delCartItem(this)" class="cart-del btn btn-default btn-xs" value="删除" />
         </td>
      </tr>
   </tbody>
   ```

4. 编写ajax和函数，

   ```js
   <script type="text/javascript">
      $(document).ready(function(){
         showCartList();
      });
   
      function showCartList(){
         //清空tbody标签中的数据
         $("#cart-list").empty();
         $.ajax({
            url: "/carts/",
            type: "get",
            dataType: "JSON",
            success: function (json){
               if (json.state==200){
                  let list=json.data;
                  for (let i = 0; i < list.length ;i++) {
                     let tr='<tr>\n' +
                           '<td>\n' +
                           '<input name="cids" value="#{cid}" type="checkbox" class="ckitem" />\n' +
                           '</td>\n' +
                           '<td><img src="..#{image}collect.png" class="img-responsive" /></td>\n' +
                           '<td>#{title}#{msg}</td>\n' +
                           '<td>¥<span id="goodsPrice#{cid}">#{singlePrice}</span></td>\n' +
                           '<td>\n' +
                           '<input id="price-#{cid}" type="button" value="-" class="num-btn" onclick="reduceNum(1)" />\n' +
                           '<input id="goodsCount#{cid}" type="text" size="2" readonly="readonly" class="num-text" value="#{num}">\n' +
                           '<input id="price+#{cid}" class="num-btn" type="button" value="+" onclick="addNum(1)" />\n' +
                           '</td>\n' +
                           '<td><span id="goodsCast#{cid}">#{totalPrice}</span></td>\n' +
                           '<td>\n' +
                           '<input type="button" onclick="delCartItem(this)" class="cart-del btn btn-default btn-xs" value="删除" />\n' +
                           '</td>\n' +
                           '</tr>';
                     tr = tr.replace(/#{cid}/g,list[i].cid);
                     tr = tr.replace(/#{image}/g,list[i].image);
                     tr = tr.replace(/#{title}/g,list[i].title);
                     tr = tr.replace(/#{msg}/g,list[i].realPrice);
                     tr = tr.replace(/#{num}/g,list[i].num);
                     tr = tr.replace(/#{singlePrice}/g,list[i].price);
                     tr = tr.replace(/#{totalPrice}/g,list[i].price * list[i].num);
   
                     $("#cart-list").append(tr);
                  }
               }
            },
            error: function (xhr){
               alert("购物车列表数据加载产生未知的异常,"+xhr.message)
            }
         });
      }
   
      // $(function() {
      //     //返回链接
      //     $(".link-account").click(function() {
      //        location.href = "orderConfirm.html";
      //     })
      // })
   </script>
   ```

5. 测试

### 增加购物车商品数量

#### 增加购物车商品数量-持久层

1. 规划sql语句

   1. 执行更新t_cart表记录的num的值，无需重复开发

   2. 根据cid的值来查询当前的购物车这条数据是否存在

      ```sql
      select * from t_cart where cid=#{cid}
      ```

2. 编写CartMapper

   ```java
   /**
    * 根据购物车cid的值来查询数据是否存在
    * @param cid   购物车商品的id
    * @return  查询到的购物车内的数据
    */
   Cart findByCid(Integer cid);
   ```

3. 编写CartMapper.xml

   ```xml
   <select id="findByCid" resultType="Cart">
       select * from t_cart where cid=#{cid}
   </select>
   ```

4. 单元测试

   ```java
   @Test
   public void findByCid(){
       Cart byCid = cartMapper.findByCid(2);
       System.out.println(byCid);
   }
   ```

#### 增加购物车商品数量-业务层

1. 规划异常,之前已经写过

   1. 更新时可能产生异常

   2. 查询的数据是否有访问的权限

   3. 要查询的数据不存在，抛出CartNotFoundException

      ```java
      public class CartNotFoundException extends ServiceException{
          public CartNotFoundException() {
          }
      
          public CartNotFoundException(String message) {
              super(message);
          }
      
          public CartNotFoundException(String message, Throwable cause) {
              super(message, cause);
          }
      
          public CartNotFoundException(Throwable cause) {
              super(cause);
          }
      
          public CartNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
              super(message, cause, enableSuppression, writableStackTrace);
          }
      }
      ```

2. 编写CartService

   ```java
   /**
    *更新用户的购物车数据的数量
    * @param cid
    * @param uid
    * @param username
    * @return 增加后的数量
    */
   Integer addNum(Integer cid,Integer uid,String username);
   ```

3. 编写CartServiceImpl

   ```java
   @Override
   public Integer addNum(Integer cid, Integer uid, String username) {
       Cart result = cartMapper.findByCid(cid);
       if(result == null){
           throw new CartNotFoundException("数据不存在");
       }
       if(!result.getUid().equals(uid)){
           throw new AccessDeniedException("数据非法访问");
       }
       Integer integer = cartMapper.updateNumByCid(cid, result.getNum() + 1, username, new Date());
       if (integer!=1){
           throw new UpdateException("更新数据失败");
       }
       return result.getNum() + 1;
   }
   ```

4. 单元测试

   ```java
   @Test
   public void addNum(){
       cartService.addNum(3,10,"管理员");
   }
   ```

#### 增加购物车商品数量-控制层

1. 处理异常

   ```java
   else if(e instanceof CartNotFoundException){
       result.setState(4007);
       result.setMessage("购物车数据不存在的异常");
   }
   ```

2. 编写控制器

   ```java
   @RequestMapping("/{cid}/num/add")
   public JsonResult<Integer> addNum(HttpSession session,Integer cid){
       Integer data = cartService.addNum(cid, getuidFromSession(session), getUsernameFromSession(session));
       return new JsonResult<>(OK,data);
   }
   ```

3. 测试http://localhost:8080/carts/3/num/add

   

#### 增加购物车商品数量-前端页面

1. 修改按钮的点击事件

   ```html
   <input id="price+#{cid}" class="num-btn" type="button" value="+" onclick="addNum(#{cid})" />
   ```

2. 编写函数和ajax

   ```js
   function addNum(cid){
      $.ajax({
         url: "/carts/"+cid+"/num/add",
         type: "POST",
         dataType: "JSON",
         success: function (json){
            if (json.state==200){
               $("#goodsCount"+cid).val(json.data);
               //获取某个标签内部的内容：文本或标签
               let price = $("#goodsPrice"+cid).html();
               let totalPrice = price * json.data;
               $("#goodsCast"+cid).html(totalPrice);
            }else{
               alert("增加购物车商品数量失败")
            }
         },
         error: function (xhr){
            alert("添加购物车商品数量产生未知的异常,"+xhr.message)
         }
      });
   }
   ```

3. 测试



### 显示勾选的购物车数据

#### 显示勾选的购物车数据-持久层

1. 规划sql语句

   1. 点击结算按钮后展示用户在上个页面所勾选的购物车对应的数据，需要讲用户勾选的多个cid传给下个页面

      ```sql
      select cid,uid,pid,t_cart.price,t_cart.num,t_product.title,t_product.image,t_product.price as realprice
      from t_cart left join t_product on t_cart.pid=t_product.id
      where cid in (?,?,?)
      order by t_cart.createdTime desc
      ```

2. 编写CartMapper

   ```java
   /**
    * 根据购物车中勾选的商品的cid来查询
    * @param cids 商品的cid集合
    * @return  查询到的勾选的数据
    */
   List<CartVo> findVOByCid(@Param("cids") List<Integer> cids);
   ```

3. 编写CartMapper.xml

   ```xml
   <select id="findVOByCid" resultType="com.zgg.store.vo.CartVo">
       select cid,uid,pid,t_cart.price,t_cart.num,t_product.title,t_product.image,t_product.price as realprice
       from t_cart left join t_product on t_cart.pid=t_product.id
       where cid in
       <foreach collection="cids" item="cid" separator="," open="(" close=")">
           #{cid}
       </foreach>
       order by t_cart.created_time desc
   </select>
   ```

4. 单元测试

   ```java
   @Test
   public void findVOByCid(){
       ArrayList<Integer> integers = new ArrayList<>();
       integers.add(1);
       integers.add(2);
       integers.add(3);
       System.out.println(cartMapper.findVOByCid(integers));
   }
   ```

#### 显示勾选的购物车数据-业务层

1. 规划异常,没有需要进行规划的异常

2. 编写CartService

   ```java
   List<CartVo> getVOByCid(List<Integer> cids,Integer uid);
   ```

3. 编写CartServiceImpl

   ```java
   @Override
   public List<CartVo> getVOByCid(List<Integer> cids, Integer uid) {
       List<CartVo> list = cartMapper.findVOByCid(cids);
       Iterator<CartVo> it = list.iterator();
       while (it.hasNext()){
           CartVo cartVo = it.next();
           if(!cartVo.getUid().equals(uid)){
               //从集合中移除这个元素
               //必须使用迭代器的remove
               it.remove();
           }
       }
       return list;
   }
   ```

4. 单元测试

   ```java
   @Test
   public void getVOByCid(){
       ArrayList<Integer> integers = new ArrayList<>();
       integers.add(1);
       integers.add(2);
       integers.add(3);
       System.out.println(cartService.getVOByCid(integers,10));
   }
   ```

#### 显示勾选的购物车数据-控制层

1. 处理异常，无需要处理的异常

2. 编写控制器

   ```java
   @RequestMapping("/list")
   //默认是数组形式传递，如果需要传递list，则需要使用@RequestParam注解
   public JsonResult<List<CartVo>> getVOByCid(@RequestParam List<Integer> cids, HttpSession session){
       List<CartVo> data = cartService.getVOByCid(cids,getuidFromSession(session));
       return new JsonResult<>(OK,data);
   }
   ```

3. 测试http://localhost:8080/carts/list?cids=1&cids=2&cids=3

   

#### 显示勾选的购物车数据-前端页面

> orderConfirm.html

1. 将cart.html页面中的结算按钮修改属性

   ```html
   <input type="submit" value="  结  算  " class="btn btn-primary btn-lg link-account" />
   ```

2. 修改跳转表单位get请求

   ```html
   <form action="orderConfirm.html" method="get" role="form">
   ```

3. 注释orderConfirm.html中引入的js文件

   ```js
   <!--       <script src="../js/orderConfirm.js" type="text/javascript" charset="utf-8"></script>-->
   ```

4. 编写函数和ajax语句

   ```js
   <script type="text/javascript">
      $(document).ready(function(){
         showCartList();
      });
   
      function showCartList(){
         //清空tbody标签中的数据
         $("#cart-list").empty();
         $.ajax({
            url: "/carts/list",
            type: "get",
            data: location.search.substr(1),//0代表问号前，1代表问号后
            dataType: "JSON",
            success: function (json){
               if (json.state==200){
                  let allCount =0;
                  let allPrice =0;
                  let list=json.data;
                  for (let i = 0; i < list.length ;i++) {
                     let tr='<tr>\n' +
                           '<td><img src="..#{image}collect.png" class="img-responsive" /></td>\n' +
                           '<td>#{title}</td>\n' +
                           '<td>¥<span>#{price}</span></td>\n' +
                           '<td>#{num}</td>\n' +
                           '<td><span>#{totalPrice}</span></td>\n' +
                           '</tr>';
                     tr = tr.replace(/#{image}/g,list[i].image);
                     tr = tr.replace(/#{title}/g,list[i].title);
                     tr = tr.replace(/#{price}/g,list[i].price);
                     tr = tr.replace(/#{num}/g,list[i].num);
                     tr = tr.replace(/#{totalPrice}/g,list[i].totalPrice);
                     $("#cart-list").append(tr);
                     allCount+=list[i].num;
                     allPrice+=list[i].price * list[i].num;
                  }
                  $("#all-count").html(allCount);
                  $("#all-price").html(allPrice);
               }
            },
            error: function (xhr){
               alert("购物车列表数据加载产生未知的异常,"+xhr.message)
            }
         });
      }
   </script>
   ```

### 确定订单页显示收货地址

#### 前端页面

> orderConfirm.html

1. 收货地址存放在一个select下拉列表中，将查询到的当前登录用户的收货地址动态的加载到这个下拉列表中。之前已经开发过通过uid查询收货地址的数据

2. orderConfirm.html页面中，收货地址数据的展示需要自动进行加载，编写相关函数

   ```js
   $(document).ready(function(){
      showCartList();
      showAddressList();
   });
   ```

3. 编写函数和ajax

   ```js
   function showAddressList(){
      //清空tbody标签中的数据
      $("#address-list").empty();
      $.ajax({
         url: "/addresses/",
         type: "get",
         dataType: "JSON",
         success: function (json){
            if (json.state==200){
               let list=json.data;
               for (let i = 0; i < list.length ;i++) {
                  let opt='<option value="#{aid}">#{name}&nbsp;&nbsp;&nbsp;#{tag}&nbsp;&nbsp;&nbsp;#{provinceName}#{cityName}#{areaName}#{address}&nbsp;&nbsp;&nbsp;#{phone}</option>';
                  opt=opt.replace(/#{aid}/g,list[i].aid);
                  opt=opt.replace(/#{name}/g,list[i].name);
                  opt=opt.replace(/#{tag}/g,list[i].tag);
                  opt=opt.replace(/#{provinceName}/g,list[i].provinceName);
                  opt=opt.replace(/#{cityName}/g,list[i].cityName);
                  opt=opt.replace(/#{areaName}/g,list[i].areaName);
                  opt=opt.replace(/#{address}/g,list[i].address);
                  opt=opt.replace(/#{phone}/g,list[i].phone);
   
                  $("#address-list").append(opt);
               }
            }
         },
         error: function (xhr){
            alert("购物车收货地址列表数据加载产生未知的异常,"+xhr.message)
         }
      });
   }
   ```

### 创建订单

#### 创建订单-数据库

```sql
CREATE TABLE t_order (
	oid INT AUTO_INCREMENT COMMENT '订单id',
	uid INT NOT NULL COMMENT '用户id',
	recv_name VARCHAR(20) NOT NULL COMMENT '收货人姓名',
	recv_phone VARCHAR(20) COMMENT '收货人电话',
	recv_province VARCHAR(15) COMMENT '收货人所在省',
	recv_city VARCHAR(15) COMMENT '收货人所在市',
	recv_area VARCHAR(15) COMMENT '收货人所在区',
	recv_address VARCHAR(50) COMMENT '收货详细地址',
	total_price BIGINT COMMENT '总价',
	STATUS INT COMMENT '状态：0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成',
	order_time DATETIME COMMENT '下单时间',
	pay_time DATETIME COMMENT '支付时间',
	created_user VARCHAR(20) COMMENT '创建人',
	created_time DATETIME COMMENT '创建时间',
	modified_user VARCHAR(20) COMMENT '修改人',
	modified_time DATETIME COMMENT '修改时间',
	PRIMARY KEY (oid)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE t_order_item (
	id INT AUTO_INCREMENT COMMENT '订单中的商品记录的id',
	oid INT NOT NULL COMMENT '所归属的订单的id',
	pid INT NOT NULL COMMENT '商品的id',
	title VARCHAR(100) NOT NULL COMMENT '商品标题',
	image VARCHAR(500) COMMENT '商品图片',
	price BIGINT COMMENT '商品价格',
	num INT COMMENT '购买数量',
	created_user VARCHAR(20) COMMENT '创建人',
	created_time DATETIME COMMENT '创建时间',
	modified_user VARCHAR(20) COMMENT '修改人',
	modified_time DATETIME COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
```

#### 创建订单-实体类

1. 订单实体类

   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class Order extends BaseEntity{
       private Integer oid;//订单id
       private Integer uid;//用户id
       private String recvName;//收货人姓名
       private String recvPhone;//收货人电话
       private String recvProvince;//收货人所在省
       private String recvCity;//收货人所在市
       private String recvArea;//收货人所在区
       private String recvAddress;//收货详细地址
       private Long totalPrice;//总价
       private Integer status;//状态：0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
       private Date orderTime;//下单时间
       private Date payTime;//支付时间
   }
   ```

2. 订单项的实体类

   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class OrderItem extends BaseEntity{
       private Integer id;//订单中的商品记录的id
       private Integer oid;//所归属的订单的id
       private Integer pid;//商品的id
       private String title;//商品标题
       private String image;//商品图片
       private Long price;//商品价格
       private Integer num;//购买数量
   }
   ```

#### 创建订单-持久层

1. 规划sql语句

   1. 将数据插入到订单表中

      ```sql
      insert into t_order (oid外所有的字段) values (字段的值)
      ```

   2. 将数据插入到订单项的表中

      ```sql
      insert into t_order_item (id外所有的字段) values (字段的值)
      ```

2. 编写OrderMapper

   ```JAVA
   public interface OrderMapper {
       /**
        * 插入订单数据
        * @param order 订单数据
        * @return  受影响的行数
        */
       Integer insertOrder(Order order);
   
       /**
        * 插入订单项数据
        * @param orderItem 订单项数据
        * @return  受影响的行数
        */
       Integer insertOrderItem(OrderItem orderItem);
   }
   ```

3. 编写OrderMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.zgg.store.mapper.OrderMapper">
       <insert id="insertOrder" useGeneratedKeys="true" keyProperty="oid">
           insert into t_order (uid,recv_name,recv_phone,recv_province,recv_city,recv_area,recv_address,total_price,status,order_time,pay_time,created_user,created_time,modified_user,modified_time)
           values (#{uid},#{recvName},#{recvPhone},#{recvProvince},#{recvCity},#{recvArea},#{recvAddress},#{totalPrice},#{status},#{orderTime},#{payTime},#{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})
   
       </insert>
       <insert id="insertOrderItem" useGeneratedKeys="true" keyProperty="id">
           insert into t_order_item (oid,pid,title,image,price,num,created_user,created_time,modified_user,modified_time)
           values (#{oid},#{pid},#{title},#{image},#{price},#{num},#{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})
       </insert>
   
   </mapper>
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class OrderMapperTests {
       @Autowired
       OrderMapper orderMapper;
       @Test
       public void insertOrder(){
           Order order = new Order();
           order.setUid(10);
           order.setRecvName("红红");
           order.setRecvPhone("111556423");
           orderMapper.insertOrder(order);
       }
       @Test
       public void insertOrderItem(){
           OrderItem orderItem = new OrderItem();
           orderItem.setOid(1);
           orderItem.setPid(10000003);
           orderItem.setTitle("广博(GuangBo)16K115页线圈记事本子日记本文具笔记本图案随机");
           orderMapper.insertOrderItem(orderItem);
   
       }
   }
   ```

#### 创建订单-业务层

1. 规划异常

   1. 在AddressService接口中定义根据收货地址的id获取收货地址数据

      ```java
      Address getByAid(Integer aid,Integer uid);
      ```

   2. 编写实现类

      ```java
      @Override
      public Address getByAid(Integer aid,Integer uid) {
          Address address = addressMapper.findByAid(aid);
          if(address==null){
              throw new AddressNotFountException("收货地址数据不存在");
          }
          if(!address.getUid().equals(uid)){
              throw new AccessDeniedException("发发数据访问");
          }
          address.setProvinceCode(null);
          address.setCityCode(null);
          address.setAreaCode(null);
          address.setModifiedUser(null);
          address.setCreatedUser(null);
          address.setModifiedTime(null);
          address.setCreatedTime(null);
          return address;
      }
      ```

2. 编写OrderService

   ```java
   public interface OrderService {
       Order create(Integer aid, Integer uid, String username, List<Integer> cids);
   
   }
   ```

3. 编写OrderServiceImpl

   ```java
   @Service
   public class OrderServiceImpl implements OrderService {
       @Autowired
       private OrderMapper orderMapper;
       @Autowired
       private AddressService addressService;
       @Autowired
       private CartService cartService;
   
       @Override
       public Order create(Integer aid, Integer uid, String username, List<Integer> cids) {
           List<CartVo> list = cartService.getVOByCid(cids, uid);
           //计算产品的总价
           Long totalPrice=0L;
           for (CartVo cartVo : list) {
               totalPrice+=cartVo.getRealPrice()*cartVo.getNum();
           }
   
           Address address = addressService.getByAid(aid, uid);
           Order order = new Order();
           order.setUid(uid);
           order.setRecvName(address.getName());
           order.setRecvPhone(address.getPhone());
           order.setRecvProvince(address.getProvinceName());
           order.setRecvCity(address.getCityName());
           order.setRecvArea(address.getAreaName());
           order.setRecvAddress(address.getAddress());
           //状态、总价、时间
           order.setStatus(0);
           order.setTotalPrice(totalPrice);
           order.setOrderTime(new Date());
           //日志
           order.setCreatedUser(username);
           order.setModifiedUser(username);
           order.setCreatedTime(new Date());
           order.setModifiedTime(new Date());
   
           Integer integer = orderMapper.insertOrder(order);
           if(integer!=1){
               throw new InsertException("插入数据异常");
           }
   
           //创建订单详细项的数据
   
           for (CartVo cartVo : list) {
               //创建一个订单项数据对象
               OrderItem orderItem = new OrderItem();
               orderItem.setOid(order.getOid());
               orderItem.setPid(cartVo.getPid());
               orderItem.setTitle(cartVo.getTitle());
               orderItem.setImage(cartVo.getImage());
               orderItem.setPrice(cartVo.getRealPrice());
               orderItem.setNum(cartVo.getNum());
               //日志
               orderItem.setCreatedUser(username);
               orderItem.setModifiedUser(username);
               orderItem.setCreatedTime(new Date());
               orderItem.setModifiedTime(new Date());
               //插入数据
               Integer integer1 = orderMapper.insertOrderItem(orderItem);
               if(integer1!=1){
                   throw new InsertException("插入数据异常");
               }
           }
   
           return order;
       }
   }
   ```

4. 单元测试

   ```java
   //@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
   @SpringBootTest
   //@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
   @RunWith(SpringRunner.class)
   public class OrderServiceTests {
       @Autowired
       OrderService orderService;
   
       @Test
       public void create(){
           ArrayList<Integer> list = new ArrayList<>();
           list.add(3);
           list.add(4);
           Order order = orderService.create(10, 10, "红红", list);
           System.out.println(order);
       }
   
   }
   ```

#### 创建订单-控制层

1. 处理异常，没有需要处理的异常

2. 编写控制器

   ```java
   @RestController
   @RequestMapping("/orders")
   public class OrderController extends BaseController{
       @Autowired
       private OrderService orderService;
       @RequestMapping("/create")
       public JsonResult<Order> create(Integer aid, @RequestParam List<Integer> cids, HttpSession session){
           Order data = orderService.create(aid, getuidFromSession(session), getUsernameFromSession(session), cids);
           return new JsonResult<>(OK,data);
       }
   }
   ```

3. 测试

#### 创建订单-前端页面

1. 确定订单页面中添加点击事件

   ```js
   $("#btn-create-order").click(function (){
      let aid=$("#address-list").val();
      let cids=location.search.substr(1);
      $.ajax({
         url: "/orders/create",
         type: "get",
         data:"aid="+aid+"&"+cids,
         dataType: "JSON",
         success: function (json){
            if (json.state==200){
               alert("订单创建成功");
               location.href="payment.html";
   
            }
         },
         error: function (xhr){
            alert("购物车收货地址列表数据加载产生未知的异常,"+xhr.message)
         }
      });
   });
   ```

## 统计业务方法耗时-AOP

> 检测项目所有的业务层方法的耗时（开始执行时间和结束执行之差）

### 切面方法

1. 切面方法修饰符必须是public。
2. 切面方法的返回值可以是void和Object。如果这个方法被@Around注解修饰此方法必须声明为Object类型,反之随意。
3. 切面方法的方法名称可以自定义。
4. 切面方法可以接收参数,参数是ProccedingloinPoint接口类型的参数。 但是@Aroud所修饰方法必须要传递这个参数，其他随意。

### 实现相关AOP

1. 添加依赖

   ```xml
   <dependency>
       <groupId>org.aspectj</groupId>
       <artifactId>aspectjweaver</artifactId>
   </dependency>
   <dependency>
       <groupId>org.aspectj</groupId>
       <artifactId>aspectjtools</artifactId>
   </dependency>
   ```

2. 定义一个切面类

   ```java
   @Component//将该类交由spring容器管理
   @Aspect//标记当前类为切面类
   public class TimeAspect {
   }
   ```

3. 定义切面方法，使用环绕通知的方式来编写。ProceedingJoinPoint接口表示连接点，目标方法的对象。

   ```java
   @Component//将该类交由spring容器管理
   @Aspect//标记当前类为切面类
   public class TimeAspect {
       //将当前环绕通知映射到某个页面上（指定连接点）
       @Around("execution(* com.zgg.store.service.impl.*.*(..))")
       public Object around(ProceedingJoinPoint pjp) throws Throwable {
           //先记录当前时间
           long start = System.currentTimeMillis();
           Object result = pjp.proceed();//调用目标方法
           //后记录当前时间
           long end = System.currentTimeMillis();
           System.out.println("耗时："+(end-start));
           return result;
       }
   }
   ```

4. 将当前环绕通知映射到某个页面上（指定连接点）

   ```java
   @Around("execution(* com.zgg.store.service.impl.*.*(..))")
   ```

5. 启动项目，随机取访问任意功能模块



## 自己在这基础上添加的功能

1. 购物车中商品数量的减少
2. 购物车中商品的删除
3. 购物车页面点击复选框后会显示已选商品数和总价
4. 订单页面显示

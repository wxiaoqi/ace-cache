# ace-cache
基于spring boot上的注解缓存，自带轻量级缓存管理页面。
@Cache比spring cache更轻量的缓存，采用fastjon序列化与反序列化，以json串存于缓存之中。
ace-cache可以快速用于日常的spring boot应用或服务之中，快速查看到缓存的状态。

# 使用手册
## 缓存配置
1、配置redis数据源，application.yml文件
```#redis-cache 相关
redis:
    pool:
         maxActive: 300
         maxIdle: 100
         maxWait: 1000
    host: 127.0.0.1
    port: 6379
    password:
    timeout: 2000
    # 服务或应用名
    sysname: ace
    enable: true
    database: 0
```
## 缓存开启
2、开启AOP扫描
```
@ComponentScan({"com.ace.cache"})
@EnableAspectJAutoProxy
```
## 缓存使用
3、在Service上进行@Cache注解或@CacheClear注解
# 注解说明
## 配置缓存：@Cache
 注解参数 | 类型  | 说明
 -------------  |------------- | -----
 key  | 字符串                                | 缓存表达式，动态运算出key 
 expires        | 整形            |    缓存时长，单位：分钟 
 desc           | 描述            |   缓存说明              
 parser         | Class<? extends ICacheResultParser> |  缓存返回结果自定义处理类 
 generator      | Class<? extends IKeyGenerator> |  缓存键值自定义生成类 
## 清除缓存：@CacheClear
注解参数 | 类型  | 说明
 -------------  |------------- | -----
 pre	|字符串 |	清除某些前缀key缓存
key |	字符串 |	清除某个key缓存
keys |	字符串数组 |	清除某些前缀key缓存
generator      | Class<? extends IKeyGenerator> |  缓存键值自定义生成类 
## 默认key动态表达式说明
表达式举例 | 说明 | 举例
-------------  |------------- | -----
@Cache(key="user:{1}")<br>public User getUserByAccount(String account) | {1}表示获取第一个参数值<br>{2}表示获取第二个参数值<br>……依此类推 | 若：account = ace，则：key = user:ace
@CacheClear(pre="user{1.account}")<br>User saveOrUpdate(User user)|{1}表示获取第一个参数值<br>{1.xx}表示获取第一个参数中的xxx属性|若：account=ace，则：key = user:ace
# 轻量管理端
访问地址：http://localhost:8080/cache
<br>管理端批量或前缀清除ace-cache注册的缓存，同时也可以快速预览缓存的数据内容，也可以对缓存的失效时间进行延长。
![img](http://ofsc32t59.bkt.clouddn.com/17-05-22/1495418425204.jpg)
# Demo
1、在src/main/test中展开的相关示例代码
>CacheTest是核心启动类
>>service包是缓存调用例子，包含自定义表达式和结果解析、注解的使用

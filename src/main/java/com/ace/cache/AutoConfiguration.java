package com.ace.cache;

import com.ace.cache.api.CacheAPI;
import com.ace.cache.api.impl.CacheRedis;
import com.ace.cache.config.RedisConfig;
import com.ace.cache.service.impl.BootRedisServiceImpl;
import com.ace.cache.service.impl.RedisServiceImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author ace
 * @create 2017/11/17.
 */
@ComponentScan({"com.ace.cache"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class AutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    @ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
    public RedisConfig constructBootRedisConfig() {
        RedisConfig redisConfig = new RedisConfig();
        return redisConfig;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
    public BootRedisServiceImpl constructBootRedisServiceImpl() {
        return new BootRedisServiceImpl();
    }

    /****** 旧的Redis配置 *****/

    @Bean
    @ConditionalOnProperty(name = "redis.enable", havingValue = "true")
    public RedisServiceImpl constructRedisServiceImpl() {
        return new RedisServiceImpl();
    }

    @Bean
    @ConfigurationProperties(prefix = "redis")
    @ConditionalOnProperty(name = "redis.enable", havingValue = "true")
    public RedisConfig constructRedisConfig() {
        RedisConfig redisConfig = new RedisConfig();
        return redisConfig;
    }

    @Bean
    @ConditionalOnProperty(name = "redis.enable", havingValue = "true")
    public JedisPoolConfig constructJedisPoolConfig(RedisConfig redisConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(Integer.parseInt(redisConfig.getPool().getMaxActive()));
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(Integer.parseInt(redisConfig.getPool().getMaxIdle()));
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(Integer.parseInt(redisConfig.getPool().getMaxWait()));
        config.setTestOnBorrow(true);
        return config;
    }

    @Bean(name = "pool")
    @ConditionalOnProperty(name = "redis.enable", havingValue = "true")
    public JedisPool constructJedisPool(JedisPoolConfig poolConfig, RedisConfig redisConfig) {
        String ip = redisConfig.getHost();
        int port = Integer.parseInt(redisConfig.getPort());
        String password = redisConfig.getPassword();
        int timeout = Integer.parseInt(redisConfig.getTimeout());
        int database = Integer.parseInt(redisConfig.getDatabase());
        if (StringUtils.isBlank(password)) {
            return new JedisPool(poolConfig, ip, port, timeout, null, database);
        } else {
            return new JedisPool(poolConfig, ip, port, timeout, password, database);
        }
    }

    @Bean
    @ConditionalOnBean(RedisConfig.class)
    public CacheAPI cacheRedis() {
        return new CacheRedis();
    }

}

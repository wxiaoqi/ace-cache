package com.ace.cache.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
	@Autowired
	private Environment env;
	
	private JedisPool pool;
	
	@Bean
	public JedisPoolConfig constructJedisPoolConfig(){
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(Integer.parseInt(env.getProperty("redis.maxActive")));
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(Integer.parseInt(env.getProperty("redis.maxIdle")));
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(Integer.parseInt(env.getProperty("redis.maxWait")));
		config.setTestOnBorrow(true);
		
		return config;
	}
	
	@Bean(name="pool")
	public JedisPool constructJedisPool(){
		String ip = env.getProperty("redis.host");
		int port = Integer.parseInt(env.getProperty("redis.port"));
		String password = env.getProperty("redis.password");
		int timeout =  Integer.parseInt(env.getProperty("redis.timeout"));
		int database = Integer.parseInt(env.getProperty("redis.database"));
		if(null == pool){
			if(StringUtils.isBlank(password)){
				pool = new JedisPool(constructJedisPoolConfig(), ip, port,timeout);
			}else{
				pool = new JedisPool(constructJedisPoolConfig(), ip, port,timeout, password,database);
			}
		}
		return pool;
	}
}

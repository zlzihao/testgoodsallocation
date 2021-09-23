package cn.nome.saas.cart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.redis.host}")
    private String host;  // Redis服务器地址
    @Value("${spring.redis.port}")
    private int port;  // Redis服务器连接端口
    @Value("${spring.redis.password}")
    private String password;  // Redis服务器连接密码（默认为空）
//    @Value("${spring.redis.timeout}")
//    private int timeout;  // 连接超时时间（毫秒）
    @Value("${spring.redis.database}")
    private int database;  // 连接超时时间（毫秒）
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxTotal;  // 连接池最大连接数（使用负值表示没有限制）
    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWaitMillis;  // 连接池最大阻塞等待时间（使用负值表示没有限制）
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;  // 连接池中的最大空闲连接
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;  // 连接池中的最小空闲连接


//    /**
//     * 配置JedisPoolConfig
//     * @return JedisPoolConfig实体
//     */
//    @Bean(name = "jedisPoolConfig")
//    public JedisPoolConfig jedisPoolConfig() {
//        log.info("初始化JedisPoolConfig");
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(this.maxTotal);  //  连接池最大连接数（使用负值表示没有限制）
//        jedisPoolConfig.setMaxWaitMillis(this.maxWaitMillis);  // 连接池最大阻塞等待时间（使用负值表示没有限制）
//        jedisPoolConfig.setMaxIdle(this.maxIdle);  // 连接池中的最大空闲连接
//        jedisPoolConfig.setMinIdle(this.minIdle);  // 连接池中的最小空闲连接
////        jedisPoolConfig.setTestOnBorrow(true);
////        jedisPoolConfig.setTestOnCreate(true);
////        jedisPoolConfig.setTestWhileIdle(true);
//        return jedisPoolConfig;
//    }
//
//    /**
//     * 实例化 RedisConnectionFactory 对象
//     * @param poolConfig
//     * @return
//     */
//    @Bean(name = "jedisConnectionFactory")
//    public RedisConnectionFactory jedisConnectionFactory(@Qualifier(value = "jedisPoolConfig") JedisPoolConfig poolConfig) {
//        log.info("初始化RedisConnectionFactory");
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
//        jedisConnectionFactory.setHostName(this.host);
//        jedisConnectionFactory.setPort(this.port);
//        jedisConnectionFactory.setDatabase(this.database);
//        return jedisConnectionFactory;
//    }
//
//    /**
//     *  实例化 RedisTemplate 对象
//     * @return
//     */
//    @Bean(name = "redisTemplate")
//    public RedisTemplate<String, String> functionDomainRedisTemplate(@Qualifier(value = "jedisConnectionFactory") RedisConnectionFactory factory) {
//        log.info("初始化RedisTemplate");
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.afterPropertiesSet();
//        redisTemplate.setEnableTransactionSupport(true);
//        return redisTemplate;
//    }

//    @Bean
//    public Jedis jedisCluster() {
//        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
//
//        JedisPool pool = new JedisPool(jedisPoolConfig, host, port, 5000);
//
//        log.info("REDIS CONFIG,host:{},port:{}",host,port);
//
//        return pool.getResource();
//    }

    @Bean
    public JedisPool jedisCluster() {
        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();

        JedisPool pool = new JedisPool(jedisPoolConfig, host, port, 5000,password);

        log.info("REDIS CONFIG,host:{},port:{}", host, port);

        return pool;
    }

    private JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.maxTotal);  //  连接池最大连接数（使用负值表示没有限制）
        jedisPoolConfig.setMaxWaitMillis(this.maxWaitMillis);  // 连接池最大阻塞等待时间（使用负值表示没有限制）
        jedisPoolConfig.setMaxIdle(this.maxIdle);  // 连接池中的最大空闲连接
        jedisPoolConfig.setMinIdle(this.minIdle); // 连接池中的最小空闲连接

        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestOnBorrow(true);
        return jedisPoolConfig;
    }
}
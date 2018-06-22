package xwv.server.container.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisDataException;
import xwv.jedis.AutoCloseJedisPool;

@Configuration
@PropertySource(value = "classpath:/cache.properties", name = "cache")
public class CacheConfig {


    @Value("${redis.host:127.0.0.1}")
    private String host = "127.0.0.1";

    @Value("${redis.port:6379}")
    private int port = 6379;

    @Value("${redis.password:}")
    private String password;

    @Value("${redis.maxIdle:20}")
    private int maxIdle;

    @Value("${redis.minIdle:1}")
    private int minIdle;

    @Bean
    public AutoCloseJedisPool jedis() {
        System.out.println();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxIdle);
        String connect_host = "127.0.0.1";
        int connect_port = 6379;


        AutoCloseJedisPool jedis = new AutoCloseJedisPool(config, connect_host);
        try {
            jedis.call(Jedis::ping);
        } catch (JedisDataException e) {
            jedis.close();
            jedis.destroy();
            connect_host = host;
            jedis = new AutoCloseJedisPool(config, connect_host, password);
        }
        System.out.println("Jedis:" + connect_host + ":" + connect_port);
        System.out.println(jedis.call(Jedis::ping));
        System.out.println();
        return jedis;

    }
}

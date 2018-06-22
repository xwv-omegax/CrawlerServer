package xwv.server.container.config;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;
import xwv.database.AutoCloseDataSource;

import java.sql.SQLException;

@Configuration
@PropertySource(value = "classpath:/database.properties", name = "database")
public class DatabaseConfig {

    @Value("${db.driver}")
    private String driver;
    @Value("${db.url}")
    private String url;
    @Value("${db.user}")
    private String user;
    @Value("${db.password}")
    private String password;
    @Value("${db.initialSize}")
    private int initialSize;
    @Value("${db.maxTotal}")
    private int maxTotal;
    @Value("${db.maxIdle}")
    private int maxIdle;
    @Value("${db.minIdle}")
    private int minIdle;
    @Value("${db.maxWaitMillis}")
    private long maxWaitMillis;

    @Autowired
    private Environment env;
//
//
//    @Bean
//    public BasicDataSource dataSource() {
//
//
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(driver);
//        dataSource.setUrl(url);
//        dataSource.setUsername(user);
//        dataSource.setPassword(password);
//        dataSource.setInitialSize(initialSize);
//        dataSource.setMaxTotal(maxTotal);
//        dataSource.setMaxIdle(maxIdle);
//        dataSource.setMinIdle(minIdle);
//        dataSource.setMaxWaitMillis(maxWaitMillis);
//        return dataSource;
//    }
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException {
//        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource());
//        sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources("classpath*:mappers/*.xml"));
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.ias.model");//别名，让*Mpper.xml实体类映射可以不加上具体包名
//        return sqlSessionFactoryBean;
//    }
//
//    @Bean(name = "transactionManager")
//    public DataSourceTransactionManager dataSourceTransactionManager() {
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
//        dataSourceTransactionManager.setDataSource(dataSource());
//        return dataSourceTransactionManager;
//    }
//
//    @Bean
//    public static MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setBasePackage("xwv.ias.model.mapper");
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//        return mapperScannerConfigurer;
//    }


    @Bean(name = "sqlite")
    public AutoCloseDataSource SqliteDataSource() throws SQLException {
        return new AutoCloseDataSource(new SQLiteDataSource() {{
            setUrl(JDBC.PREFIX + "pixiv.db");
        }});
    }

    @Bean(name = "mysql")
    public AutoCloseDataSource MysqlDataSource() throws SQLException {
        System.out.println();
        System.out.println("Mysql URL:" + url);
        System.out.println("Mysql User:" + user);
        System.out.println();
        return new AutoCloseDataSource(new BasicDataSource() {{
            setDriverClassName(driver);
            setUrl(url);
            setUsername(user);
            setPassword(password);
            setInitialSize(initialSize);
            setMaxTotal(maxTotal);
            setMaxIdle(maxIdle);
            setMinIdle(minIdle);
            setMaxWaitMillis(maxWaitMillis);
        }});
    }

}

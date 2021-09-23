package cn.nome.saas.sdc.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;

import static cn.nome.saas.sdc.datasource.MainDataSourceConfig.PACKAGE;


/**
 * @author lizihao@nome.com
 */

@Configuration
@MapperScan(basePackages = PACKAGE, sqlSessionFactoryRef = "mainSqlSessionFactory")
public class MainDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(MainDataSourceConfig.class);

    // 精确到 main 目录，以便跟其他数据源隔离
    static final String PACKAGE = "cn.nome.saas.sdc.repository.dao";
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Value("${spring.db-master.url}")
    private String url;

    @Value("${spring.db-master.username}")
    private String user;

    @Value("${spring.db-master.password}")
    private String password;

    @Value("${spring.db-master.driver-class-name}")
    private String driverClass;

    @Primary // 表示这个数据源是默认数据源, 这个注解必须要加，因为不加的话spring将分不清楚那个为主数据源（默认数据源）
    @Bean("mainDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.main-db") //读取application.yml中的配置参数映射成为一个对象
    public DataSource getMainDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        Connection conn;
        try {
            conn = dataSource.getConnection();
            if (conn != null && conn.isValid(2000)) {
                logger.info("[INIT_MYSQL] msg=url:{},isConnected:{}", url, "true");
            }
        } catch (Exception e) {
        }
        return dataSource;
    }

    @Primary
    @Bean(name = "mainTransactionManager")
    public DataSourceTransactionManager mainTransactionManager() {
        return new DataSourceTransactionManager(getMainDataSource());
    }

    @Primary
    @Bean(name = "mainSqlSessionFactory")
    public SqlSessionFactory mainSqlSessionFactory(@Qualifier("mainDataSource") DataSource mainDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(mainDataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}

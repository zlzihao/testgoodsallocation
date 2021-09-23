package cn.nome.saas.sdc.datasource;

import cn.nome.platform.common.logger.LoggerUtil;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@MapperScan(basePackages = VerticaDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "verticaSqlSessionFactory")
public class VerticaDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(VerticaDataSourceConfig.class);

    // 精确到 vertica 目录，以便跟其他数据源隔离
    static final String PACKAGE ="cn.nome.saas.sdc.bigData.repository.dao";
    static final String MAPPER_LOCATION ="classpath:mapper/bigdata/*.xml";

    @Value("${vertica.datasource.url}")
    private String url;

    @Value("${vertica.datasource.username}")
    private String user;

    @Value("${vertica.datasource.password}")
    private String password;

    @Value("${vertica.datasource.driverClassName}")
    private String driverClass;

    @Value("${vertica.maxActive:20}")
    private int MaxActive;

    @Value("${vertica.initialSize:5}")
    private int InitialSize;

    @Value("${vertica.maxWait:30000}")
    private int MaxWait;

    @Value("${vertica.minIdle:5}")
    private int MinIdle;

    @Value("${vertica.timeBetweenEvictionRunsMillis:60000}")
    private int TimeBetweenEvictionRunsMillis;

    @Value("${vertica.minEvictableIdleTimeMillis:30000}")
    private int MinEvictableIdleTimeMillis;

    @Value("${vertica.testWhileIdle:true}")
    private boolean MinTestWhileIdle;

    @Value("${vertica.testOnReturn:false}")
    private boolean MinTestOnReturn;

    @Value("${vertica.testOnBorrow:false}")
    private boolean MinTestOnBorrow;

    @Value("${vertica.poolPreparedStatements:true}")
    private boolean PoolPreparedStatements;

    @Value("${vertica.maxOpenPreparedStatements:20}")
    private int MaxOpenPreparedStatements;

    @Bean(name = "verticaDataSource")
    public DataSource verticaDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaxActive(MaxActive);
        dataSource.setInitialSize(InitialSize);
        dataSource.setMaxWait(MaxWait);
        dataSource.setMinIdle(MinIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(TimeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(MinEvictableIdleTimeMillis);
        dataSource.setTestWhileIdle(MinTestWhileIdle);
        dataSource.setTestOnBorrow(MinTestOnBorrow);
        dataSource.setTestOnReturn(MinTestOnReturn);
        dataSource.setPoolPreparedStatements(PoolPreparedStatements);
        dataSource.setValidationQuery("select 'x'");
        dataSource.setPoolPreparedStatements(PoolPreparedStatements);
        dataSource.setMaxOpenPreparedStatements(MaxOpenPreparedStatements);

        Connection conn ;
        try {
            conn = dataSource.getConnection();
            if (conn != null && conn.isValid(2000)) {
                LoggerUtil.info(logger,"[INIT_VERTICAL] msg=url:{0},isConnected:{1}",url,"true");
            }
        } catch (Exception e) {
        }
        return dataSource;
    }

    @Bean(name = "verticaTransactionManager")
    public DataSourceTransactionManager verticaTransactionManager() {
        return new DataSourceTransactionManager(verticaDataSource());
    }

    @Bean(name = "verticaSqlSessionFactory")
    public SqlSessionFactory verticaSqlSessionFactory(@Qualifier("verticaDataSource") DataSource verticaDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(verticaDataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(VerticaDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
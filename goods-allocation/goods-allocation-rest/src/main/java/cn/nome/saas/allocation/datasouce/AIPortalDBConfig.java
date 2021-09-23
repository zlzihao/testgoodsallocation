package cn.nome.saas.allocation.datasouce;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * VerticalDBConfig
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Configuration
@MapperScan(basePackages = "cn.nome.saas.allocation.repository.dao.aiPortal", sqlSessionTemplateRef  = "sqlSessionTemplateAiPortal")
public class AIPortalDBConfig {

    @Value("${ai_portal.datasource.url}")
    private String url;

    @Value("${ai_portal.datasource.username}")
    private String userName;

    @Value("${ai_portal.datasource.password}")
    private String password;

    @Value("${ai_portal.datasource.driverClassName}")
    private String driverClass;

    @Value("${ai_portal.datasource.maxActive}")
    private int maxActive;

    @Value("${ai_portal.datasource.initialSize}")
    private int nitialSize;

    @Value("${ai_portal.datasource.maxWait}")
    private int maxWait;

    @Value("${ai_portal.datasource.minIdle}")
    private int minIdle;

    @Value("${ai_portal.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${ai_portal.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Bean
    public DataSource aiPortalDBSource() {

        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClass);
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(nitialSize);
        dataSource.setMaxWait(maxWait);
        dataSource.setMinIdle(minIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("select 'x'");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);

        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryAiPortal")
    public SqlSessionFactory sqlSessionFactoryAiPortal(DataSource aiPortalDBSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(aiPortalDBSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/aiPortal/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplateAiPortal")
    public SqlSessionTemplate sqlSessionTemplateAiPortal(SqlSessionFactory sqlSessionFactoryAiPortal) {
        return new SqlSessionTemplate(sqlSessionFactoryAiPortal);
    }

    @Bean(name = "aiPortalTransactionManager")
    public DataSourceTransactionManager aiPortalTransactionManager(DataSource aiPortalDBSource) {
        return new DataSourceTransactionManager(aiPortalDBSource);
    }
}

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
@MapperScan(basePackages = "cn.nome.saas.allocation.repository.dao.portal", sqlSessionTemplateRef  = "sqlSessionTemplatePortal")
public class PortalDBConfig {

    @Value("${portal.datasource.url}")
    private String url;

    @Value("${portal.datasource.username}")
    private String userName;

    @Value("${portal.datasource.password}")
    private String password;

    @Value("${portal.datasource.driverClassName}")
    private String driverClass;

    @Value("${portal.datasource.maxActive}")
    private int maxActive;

    @Value("${portal.datasource.initialSize}")
    private int nitialSize;

    @Value("${portal.datasource.maxWait}")
    private int maxWait;

    @Value("${portal.datasource.minIdle}")
    private int minIdle;

    @Value("${portal.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${portal.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Bean
    public DataSource portalDBSource() {

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

    @Bean(name = "sqlSessionFactoryPortal")
    public SqlSessionFactory sqlSessionFactoryVertical(DataSource portalDBSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(portalDBSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/portal/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplatePortal")
    public SqlSessionTemplate sqlSessionTemplateAllocation(SqlSessionFactory sqlSessionFactoryPortal) {
        return new SqlSessionTemplate(sqlSessionFactoryPortal);
    }

    @Bean(name = "portalTransactionManager")
    public DataSourceTransactionManager portalTransactionManager() {
        return new DataSourceTransactionManager(portalDBSource());
    }
}

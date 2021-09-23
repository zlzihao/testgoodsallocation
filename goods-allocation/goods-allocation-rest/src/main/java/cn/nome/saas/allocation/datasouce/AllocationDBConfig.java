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
 * AllocationDBConfig
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Configuration
@MapperScan(basePackages = "cn.nome.saas.allocation.repository.dao.allocation", sqlSessionTemplateRef  = "sqlSessionTemplateAllocation")
public class AllocationDBConfig {

    @Value("${allocation.datasource.url}")
    private String url;

    @Value("${allocation.datasource.username}")
    private String userName;

    @Value("${allocation.datasource.password}")
    private String password;

    @Value("${allocation.datasource.driverClassName}")
    private String driverClass;

    @Value("${allocation.datasource.maxActive}")
    private int maxActive;

    @Value("${allocation.datasource.initialSize}")
    private int nitialSize;

    @Value("${allocation.datasource.maxWait}")
    private int maxWait;

    @Value("${allocation.datasource.minIdle}")
    private int minIdle;

    @Value("${allocation.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${allocation.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Bean
    public DataSource allocationDBSource() {

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

    @Bean(name = "sqlSessionFactoryAllocation")
    public SqlSessionFactory sqlSessionFactoryAllocation(DataSource allocationDBSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(allocationDBSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/allocation/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplateAllocation")
    public SqlSessionTemplate sqlSessionTemplateAllocation(SqlSessionFactory sqlSessionFactoryAllocation) {
        return new SqlSessionTemplate(sqlSessionFactoryAllocation);
    }

    @Bean(name = "allocationTransactionManager")
    public DataSourceTransactionManager allocationTransactionManager() {
        return new DataSourceTransactionManager(allocationDBSource());
    }

}
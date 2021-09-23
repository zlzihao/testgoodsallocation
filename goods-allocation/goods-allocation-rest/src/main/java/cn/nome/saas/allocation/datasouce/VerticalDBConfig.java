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
@MapperScan(basePackages = "cn.nome.saas.allocation.repository.dao.vertical", sqlSessionTemplateRef  = "sqlSessionTemplateVertical")
public class VerticalDBConfig {

    @Value("${vertical.datasource.url}")
    private String url;

    @Value("${vertical.datasource.username}")
    private String userName;

    @Value("${vertical.datasource.password}")
    private String password;

    @Value("${vertical.datasource.driverClassName}")
    private String driverClass;

    @Value("${vertical.datasource.maxActive}")
    private int maxActive;

    @Value("${vertical.datasource.initialSize}")
    private int nitialSize;

    @Value("${vertical.datasource.maxWait}")
    private int maxWait;

    @Value("${vertical.datasource.minIdle}")
    private int minIdle;

    @Value("${vertical.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${vertical.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Bean
    public DataSource verticalDBSource() {

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

    @Bean(name = "sqlSessionFactoryVertical")
    public SqlSessionFactory sqlSessionFactoryVertical(DataSource verticalDBSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(verticalDBSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/vertical/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplateVertical")
    public SqlSessionTemplate sqlSessionTemplateAllocation(SqlSessionFactory sqlSessionFactoryVertical) {
        return new SqlSessionTemplate(sqlSessionFactoryVertical);
    }

    @Bean(name = "verticalTransactionManager")
    public DataSourceTransactionManager verticalTransactionManager() {
        return new DataSourceTransactionManager(verticalDBSource());
    }
}

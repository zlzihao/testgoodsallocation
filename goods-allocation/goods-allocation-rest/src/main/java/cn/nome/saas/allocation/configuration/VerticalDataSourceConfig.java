package cn.nome.saas.allocation.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

//@Configuration
//@MapperScan(basePackages = VerticalDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "verticalSqlSessionFactory")
public class VerticalDataSourceConfig {

	// 精确到 vertica 目录，以便跟其他数据源隔离
	static final String PACKAGE = "cn.nome.saas.allocation.repository.dao.vertical";
	static final String MAPPER_LOCATION = "classpath:mapper/vertical/*.xml";

	@Value("${vertical.datasource.url}")
	private String url;

	@Value("${vertical.datasource.username}")
	private String user;

	@Value("${vertical.datasource.password}")
	private String password;

	@Value("${vertical.datasource.driverClassName}")
	private String driverClass;

	@Value("${vertical.datasource.maxWait}")
	private long maxWaitMillis;

	@Bean(name = "verticalDataSource")
	public DataSource verticalDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setInitialSize(2);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(maxWaitMillis);
		return dataSource;
	}

	@Bean(name = "verticalTransactionManager")
	public DataSourceTransactionManager verticalTransactionManager() {
		return new DataSourceTransactionManager(verticalDataSource());
	}

	@Bean(name = "verticalSqlSessionFactory")
	public SqlSessionFactory verticalSqlSessionFactory(@Qualifier("verticalDataSource") DataSource verticalDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(verticalDataSource);
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(VerticalDataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
}
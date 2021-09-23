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
//@MapperScan(basePackages = AllocationDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "allocationSqlSessionFactory")
public class AllocationDataSourceConfig {

	// 精确到 location 目录，以便跟其他数据源隔离
	static final String PACKAGE = "cn.nome.saas.allocation.repository.dao.allocation";
	static final String MAPPER_LOCATION = "classpath:mapper/allocation/*.xml";

	@Value("${allocation.datasource.url}")
	private String url;

	@Value("${allocation.datasource.username}")
	private String user;

	@Value("${allocation.datasource.password}")
	private String password;

	@Value("${allocation.datasource.driverClassName}")
	private String driverClass;

	@Bean(name = "allocationDataSource")
	public DataSource allocationDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setInitialSize(2);
		dataSource.setMaxActive(10);
		return dataSource;
	}

	@Bean(name = "allocationTransactionManager")
	public DataSourceTransactionManager allocationTransactionManager() {
		return new DataSourceTransactionManager(allocationDataSource());
	}

	@Bean(name = "allocationSqlSessionFactory")
	public SqlSessionFactory allocationSqlSessionFactory(@Qualifier("allocationDataSource") DataSource allocationDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(allocationDataSource);
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(AllocationDataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
}
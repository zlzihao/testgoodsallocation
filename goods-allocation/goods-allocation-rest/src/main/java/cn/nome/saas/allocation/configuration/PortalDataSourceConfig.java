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
//@MapperScan(basePackages = PortalDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "portalSqlSessionFactory")
public class PortalDataSourceConfig {

	// 精确到 location 目录，以便跟其他数据源隔离
	static final String PACKAGE = "cn.nome.saas.allocation.repository.dao.portal";
	static final String MAPPER_LOCATION = "classpath:mapper/portal/*.xml";

	@Value("${portal.datasource.url}")
	private String url;

	@Value("${portal.datasource.username}")
	private String user;

	@Value("${portal.datasource.password}")
	private String password;

	@Value("${portal.datasource.driverClassName}")
	private String driverClass;

	@Bean(name = "portalDataSource")
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

	@Bean(name = "portalTransactionManager")
	public DataSourceTransactionManager allocationTransactionManager() {
		return new DataSourceTransactionManager(allocationDataSource());
	}

	@Bean(name = "portalSqlSessionFactory")
	public SqlSessionFactory allocationSqlSessionFactory(@Qualifier("portalDataSource") DataSource portalDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(portalDataSource);
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(PortalDataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
}
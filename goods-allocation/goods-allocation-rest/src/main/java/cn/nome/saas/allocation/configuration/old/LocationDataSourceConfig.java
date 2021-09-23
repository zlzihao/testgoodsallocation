//package cn.nome.saas.allocation.configuration.old;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//@MapperScan(basePackages = LocationDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "locationSqlSessionFactory")
//public class LocationDataSourceConfig {
//
//	// 精确到 location 目录，以便跟其他数据源隔离
//	static final String PACKAGE = "cn.nome.saas.allocation.repository.old.location.dao";
//	static final String MAPPER_LOCATION = "classpath:mapper/old/location/*.xml";
//
//	@Value("${location.datasource.url}")
//	private String url;
//
//	@Value("${location.datasource.username}")
//	private String user;
//
//	@Value("${location.datasource.password}")
//	private String password;
//
//	@Value("${location.datasource.driverClassName}")
//	private String driverClass;
//
//	@Bean(name = "locationDataSource")
//	public DataSource locationDataSource() {
//		DruidDataSource dataSource = new DruidDataSource();
//		dataSource.setDriverClassName(driverClass);
//		dataSource.setUrl(url);
//		dataSource.setUsername(user);
//		dataSource.setPassword(password);
//		dataSource.setInitialSize(2);
//		dataSource.setMaxActive(10);
//		return dataSource;
//	}
//
//	@Bean(name = "locationTransactionManager")
//	public DataSourceTransactionManager locationTransactionManager() {
//		return new DataSourceTransactionManager(locationDataSource());
//	}
//
//	@Bean(name = "locationSqlSessionFactory")
//	public SqlSessionFactory locationSqlSessionFactory(@Qualifier("locationDataSource") DataSource locationDataSource)
//			throws Exception {
//		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//		sessionFactory.setDataSource(locationDataSource);
//		sessionFactory.setMapperLocations(
//				new PathMatchingResourcePatternResolver().getResources(LocationDataSourceConfig.MAPPER_LOCATION));
//		return sessionFactory.getObject();
//	}
//}
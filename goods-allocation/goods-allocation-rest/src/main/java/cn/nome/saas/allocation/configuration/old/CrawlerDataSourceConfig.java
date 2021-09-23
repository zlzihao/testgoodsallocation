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
//@MapperScan(basePackages = CrawlerDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "crawlerSqlSessionFactory")
//public class CrawlerDataSourceConfig {
//
//	// 精确到 location 目录，以便跟其他数据源隔离
//	static final String PACKAGE = "cn.nome.saas.allocation.repository.old.crawler.dao";
//	static final String MAPPER_LOCATION = "classpath:mapper/old/crawler/*.xml";
//
//	@Value("${crawler.datasource.url}")
//	private String url;
//
//	@Value("${crawler.datasource.username}")
//	private String user;
//
//	@Value("${crawler.datasource.password}")
//	private String password;
//
//	@Value("${crawler.datasource.driverClassName}")
//	private String driverClass;
//
//	@Bean(name = "crawlerDataSource")
//	public DataSource crawlerDataSource() {
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
//	@Bean(name = "crawlerTransactionManager")
//	public DataSourceTransactionManager crawlerTransactionManager() {
//		return new DataSourceTransactionManager(crawlerDataSource());
//	}
//
//	@Bean(name = "crawlerSqlSessionFactory")
//	public SqlSessionFactory crawlerSqlSessionFactory(@Qualifier("crawlerDataSource") DataSource crawlerDataSource)
//			throws Exception {
//		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//		sessionFactory.setDataSource(crawlerDataSource);
//		sessionFactory.setMapperLocations(
//				new PathMatchingResourcePatternResolver().getResources(CrawlerDataSourceConfig.MAPPER_LOCATION));
//		return sessionFactory.getObject();
//	}
//}
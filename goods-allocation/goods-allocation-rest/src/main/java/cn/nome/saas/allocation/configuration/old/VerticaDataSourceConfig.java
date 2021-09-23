package cn.nome.saas.allocation.configuration.old;

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

@Configuration
@MapperScan(basePackages = VerticaDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "verticaSqlSessionFactory")
public class VerticaDataSourceConfig {

	// 精确到 vertica 目录，以便跟其他数据源隔离
	static final String PACKAGE = "cn.nome.saas.allocation.repository.old.vertica.dao";
	static final String MAPPER_LOCATION = "classpath:mapper/old/vertica/*.xml";

	@Value("${vertica.datasource.url}")
	private String url;

	@Value("${vertica.datasource.username}")
	private String user;

	@Value("${vertica.datasource.password}")
	private String password;

	@Value("${vertica.datasource.driverClassName}")
	private String driverClass;

	@Bean(name = "verticaDataSource")
	public DataSource verticaDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setInitialSize(2);
		dataSource.setMaxActive(10);
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
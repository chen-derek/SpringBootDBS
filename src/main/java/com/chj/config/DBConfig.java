package com.chj.config;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.chj.common.Constants;
import com.chj.datasource.DynamicDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.chj.persistence" })
public class DBConfig {

	final static Logger logger = LoggerFactory.getLogger(DBConfig.class);

	@Autowired
	private Environment env;

	@Bean
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory factory = entityManagerFactory().getObject();
		return new JpaTransactionManager(factory);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(Boolean.TRUE);
		vendorAdapter.setShowSql(Boolean.TRUE);

		// factory.setDataSource(dataSource());
		factory.setDataSource(getDynamicDataSource());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.chj.persistence");

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));

		// jpaProperties.put("hibernate.hbm2ddl.auto",
		// env.getProperty("hibernate.hbm2ddl.auto"));
		// jpaProperties.put("hibernate.dialect",
		// env.getProperty("hibernate.dialect"));
		// jpaProperties.put("hibernate.cache.use_second_level_cache",
		// env.getProperty("hibernate.cache.use_second_level_cache"));
		// jpaProperties.put("hibernate.cache.use_query_cache",
		// env.getProperty("hibernate.cache.use_query_cache"));
		// jpaProperties.put("hibernate.cache.use_structured_entries",
		// env.getProperty("hibernate.cache.use_structured_entries"));
		// jpaProperties.put("hibernate.generate_statistics",
		// env.getProperty("hibernate.generate_statistics"));
		// jpaProperties.put("hibernate.cache.provider_class",
		// env.getProperty("hibernate.cache.provider_class"));
		// jpaProperties.put("hibernate.cache.region.factory_class",
		// env.getProperty("hibernate.cache.region.factory_class"));

		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		return factory;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	public DataSource dataSource() {
		logger.info("jdbc.preference:" + env.getProperty("jdbc.preference"));
		// if (Constants.DATA_SOURCE_PREFERENCE_JNDI.equals(env.getProperty("jdbc.preference"))) {
		// try {
		// InitialContext ctx = new InitialContext();
		// DataSource dataSource = (DataSource) ctx.lookup(env.getProperty("jdbc.jndi"));
		// return dataSource;
		// } catch (NamingException e) {
		// e.printStackTrace();
		// }
		//
		// } else {
		// BasicDataSource dataSource = new BasicDataSource();
		// dataSource.setDriverClassName(env.getProperty(Constants.DB.DEFAULT_DB_DRIVER));
		// dataSource.setUrl(env.getProperty(Constants.DB.DEFAULT_DB_URL));
		// dataSource.setUsername(env.getProperty(Constants.DB.DEFAULT_DB_USERNAME));
		// dataSource.setPassword(env.getProperty(Constants.DB.DEFAULT_DB_PASSWORD));
		// return dataSource;
		// }
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty(MessageFormat.format(Constants.DB.DB_DRIVER, env.getProperty(Constants.DB.APP_DS_DEFAULT))));
		dataSource.setUrl(env.getProperty(MessageFormat.format(Constants.DB.DB_URL, env.getProperty(Constants.DB.APP_DS_DEFAULT))));
		dataSource.setUsername(env.getProperty(MessageFormat.format(Constants.DB.DB_USERNAME, env.getProperty(Constants.DB.APP_DS_DEFAULT))));
		dataSource.setPassword(env.getProperty(MessageFormat.format(Constants.DB.DB_PASSWORD, env.getProperty(Constants.DB.APP_DS_DEFAULT))));
		return dataSource;

	}

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("db.sql"));
		dataSourceInitializer.setDatabasePopulator(databasePopulator);
		dataSourceInitializer.setEnabled(Boolean.parseBoolean("false"));
		return dataSourceInitializer;
	}

	@Bean
	public DynamicDataSource getDynamicDataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		logger.info("jdbc.preference:" + env.getProperty("jdbc.preference"));
		String initDBStr = env.getProperty(Constants.DB.APP_DB_INIT);
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		String defaultDataSource = env.getProperty(Constants.DB.APP_DS_DEFAULT);
		if (Constants.DATA_SOURCE_PREFERENCE_JNDI.equals(env.getProperty("jdbc.preference"))) {
			List<String> dbs = Arrays.asList(initDBStr.split(","));
			try {
				InitialContext ctx = new InitialContext();
				DataSource dataSource = null;
				for (String str : dbs) {
					if (StringUtils.isEmpty(defaultDataSource)) {
						defaultDataSource = str;
					}
					dataSource = (DataSource) ctx.lookup((env.getProperty(MessageFormat.format(Constants.DB.DB_JNDI, str))));
					targetDataSources.put(str, dataSource);
					DynamicDataSource.addDataSource(str);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			if (StringUtils.isNotEmpty(initDBStr)) {
				List<String> dbs = Arrays.asList(initDBStr.split(","));
				BasicDataSource dataSource = null;
				for (String str : dbs) {
					if (StringUtils.isEmpty(defaultDataSource)) {
						defaultDataSource = str;
					}
					dataSource = new BasicDataSource();
					dataSource.setDriverClassName(env.getProperty(MessageFormat.format(Constants.DB.DB_DRIVER, str)));
					dataSource.setUrl(env.getProperty(MessageFormat.format(Constants.DB.DB_URL, str)));
					dataSource.setUsername(env.getProperty(MessageFormat.format(Constants.DB.DB_USERNAME, str)));
					dataSource.setPassword(env.getProperty(MessageFormat.format(Constants.DB.DB_PASSWORD, str)));
					targetDataSources.put(str, dataSource);
					DynamicDataSource.addDataSource(str);
				}
			}
		}
		DynamicDataSource.addDataSource(defaultDataSource);
		dynamicDataSource.setTargetDataSources(targetDataSources);
		DynamicDataSource.setDefaultDataSource(defaultDataSource);
		return dynamicDataSource;
	}
}

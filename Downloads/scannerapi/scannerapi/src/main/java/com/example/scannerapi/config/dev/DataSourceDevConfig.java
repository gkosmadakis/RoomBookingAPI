package com.example.scannerapi.config.dev;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.scannerapi.repository.dev", 
entityManagerFactoryRef = "devEntityManagerFactory",
transactionManagerRef = "transactionManagerDev")
@EnableTransactionManagement
public class DataSourceDevConfig {

	@Value("${spring.second-datasource.url}")
	private String datasourceDevUrl;

	@Value("${spring.second-datasource.username}")
	private String datasourceDevUsername;

	@Value("${spring.second-datasource.password}")
	private String datasourceDevPassword;

	@Bean
	public DataSource devDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl(datasourceDevUrl);
		dataSource.setUsername(datasourceDevUsername);
		dataSource.setPassword(datasourceDevPassword);
		return dataSource;
	}
	    
	@Bean(name = "devEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean devEntityManagerFactory(
			@Qualifier("devDataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("com.example.scannerapi.model");
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		em.setJpaProperties(properties);
		return em;
	}
	
	@Bean
    public PlatformTransactionManager transactionManagerDev(@Qualifier("devEntityManagerFactory") LocalContainerEntityManagerFactoryBean devEntityManagerFactory) {
        return new JpaTransactionManager(devEntityManagerFactory.getObject());
    }

}

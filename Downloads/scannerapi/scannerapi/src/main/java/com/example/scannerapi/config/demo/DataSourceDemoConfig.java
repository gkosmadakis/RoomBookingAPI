package com.example.scannerapi.config.demo;

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
@EnableJpaRepositories(
basePackages = "com.example.scannerapi.repository.demo",
entityManagerFactoryRef = "demoEntityManagerFactory", 
transactionManagerRef = "transactionManagerDemo")
@EnableTransactionManagement
public class DataSourceDemoConfig {

	@Value("${spring.datasource.url}")
    private String datasourceDemoUrl;

    @Value("${spring.datasource.username}")
    private String datasourceDemoUsername;

    @Value("${spring.datasource.password}")
    private String datasourceDemoPassword;
    
    @Bean
    public DataSource demoDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl(datasourceDemoUrl);
        dataSource.setUsername(datasourceDemoUsername);
        dataSource.setPassword(datasourceDemoPassword);
        return dataSource;
    }

    @Bean(name = "demoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean demoEntityManagerFactory(
          @Qualifier("demoDataSource") DataSource dataSource) {
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
    public PlatformTransactionManager transactionManagerDemo(@Qualifier("demoEntityManagerFactory") LocalContainerEntityManagerFactoryBean demoEntityManagerFactory) {
    	return new JpaTransactionManager(demoEntityManagerFactory.getObject());
    }

}

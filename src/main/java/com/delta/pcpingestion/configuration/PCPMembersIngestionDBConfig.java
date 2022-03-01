package com.delta.pcpingestion.configuration;

import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.core.env.Environment;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory",
transactionManagerRef = "transactionManager",
basePackages = { "com.delta.pcpingestion.repo" })
public class PCPMembersIngestionDBConfig {
	
    @Autowired
    private Environment env;

	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.mssql.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em
         = new LocalContainerEntityManagerFactoryBean();
       em.setDataSource(dataSource());
       em.setPackagesToScan(
         new String[] { "com.delta.pcpingestion.entity" });

       HibernateJpaVendorAdapter vendorAdapter
         = new HibernateJpaVendorAdapter();
       em.setJpaVendorAdapter(vendorAdapter);
       HashMap<String, Object> properties = new HashMap<>();
       properties.put("hibernate.hbm2ddl.auto",
         env.getProperty("spring.jpa.hibernate.ddl-auto"));
       properties.put("hibernate.dialect",
         env.getProperty("spring.jpa.properties.hibernate.dialect"));
       properties.put("spring.jpa.show-sql",
    	         env.getProperty("spring.jpa.show-sql"));
       properties.put("spring.jpa.properties.hibernate.format_sql",
  	         env.getProperty("spring.jpa.properties.hibernate.format_sql"));
       properties.put("spring.jpa.properties.hibernate.jdbc.batch_size",
    	         env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size"));
       em.setJpaPropertyMap(properties);       

       return em;
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
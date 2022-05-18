package com.delta.pcpingestion.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

//@Configuration
//@EnableTransactionManagement
//  @EnableJpaRepositories(entityManagerFactoryRef = "mtvEntityManagerFactory",
//  transactionManagerRef = "mtvTransactionManager", basePackages = {
//  "com.delta.pcpingestion.mtv.repo" })
@Slf4j
public class MTVDBConfiguration {
	
	@Autowired
    private Environment env;

//	@Bean(name = "orcaleDataSource")
//	@ConfigurationProperties(prefix = "metavance.database")
	public DataSource orcaleDataSource() {
		return DataSourceBuilder.create().build();
	}

	// @Bean(name = "mtvEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mtvEntityManagerFactoryBean(EntityManagerFactoryBuilder builder,
			@Qualifier("orcaleDataSource") DataSource dataSource) {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", 
				env.getProperty("metavance.database.hibernate.dialect"));
		properties.setProperty("hibernate.show_sql",
				env.getProperty("spring.jpa.show-sql"));
		properties.setProperty("spring.jpa.hibernate.ddl-auto",
				env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("metavance.database.jdbcUrl", 
				env.getProperty("metavance.database.jdbcUrl"));
		properties.setProperty("spring.jpa.show-sql", 
				env.getProperty("spring.jpa.show-sql"));
		properties.setProperty("spring.jpa.properties.hibernate.format_sql", 
				env.getProperty("spring.jpa.properties.hibernate.format_sql"));
		properties.setProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation", "true");
		LocalContainerEntityManagerFactoryBean emf = builder.dataSource(dataSource)
				.packages("com.delta.pcpingestion.mtv.entities").persistenceUnit("mtvPU").build();
		emf.setJpaProperties(properties);
		return emf;
	}

//	@Bean(name = "mtvTransactionManager")
	public PlatformTransactionManager mtvTransactionManager(
			final @Qualifier("mtvEntityManagerFactory") LocalContainerEntityManagerFactoryBean mtvEntityManagerFactoryBean) {
		return new JpaTransactionManager(mtvEntityManagerFactoryBean.getObject());
	}

//	@Bean(name = "mtvJdbcTemplate")
	public JdbcTemplate mtvJdbcTemplate(@Qualifier("orcaleDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}

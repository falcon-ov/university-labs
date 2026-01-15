package com.example.librarymanagement.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.datasource", dataSource)
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "update")
                .applySetting("hibernate.show_sql", "true")
                .build();

        MetadataSources sources = new MetadataSources(registry)
                .addResource("mappings/Author.hbm.xml")
                .addResource("mappings/Book.hbm.xml")
                .addResource("mappings/Category.hbm.xml")
                .addResource("mappings/Library.hbm.xml")
                .addResource("mappings/Publisher.hbm.xml");

        return sources.buildMetadata().buildSessionFactory();
    }
}
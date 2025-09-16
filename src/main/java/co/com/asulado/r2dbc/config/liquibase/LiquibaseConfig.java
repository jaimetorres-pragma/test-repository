package co.com.asulado.r2dbc.config.liquibase;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    @LiquibaseDataSource
    public DataSource liquibaseDataSource(JdbcConnectionProperties properties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(properties.driverClassName());
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%d/%s?currentSchema=%s",
                properties.host(), properties.port(), properties.database(), properties.schema()));
        dataSource.setUsername(properties.username());
        dataSource.setPassword(properties.password());
        return dataSource;
    }
}
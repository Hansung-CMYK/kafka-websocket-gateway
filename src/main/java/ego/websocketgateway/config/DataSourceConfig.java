package ego.websocketgateway.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class DataSourceConfig {
	@Bean(name = "tenantJdbcTemplate")
	public JdbcTemplate tenantJdbcTemplate(@Qualifier("dataSource") DataSource multiDs) {
		return new JdbcTemplate(multiDs);
	}
}

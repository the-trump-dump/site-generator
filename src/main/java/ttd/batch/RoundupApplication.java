package ttd.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@SpringBootApplication
@EnableConfigurationProperties(RoundupConfigurationProperties.class)
public class RoundupApplication {

		@Bean
		TransactionTemplate transactionTemplate(PlatformTransactionManager txm) {
				return new TransactionTemplate(txm);
		}

		@Bean
		JdbcTemplate jdbcTemplate(DataSource ds) {
				return new JdbcTemplate(ds);
		}

		public static void main(String[] args) {
				SpringApplication.run(RoundupApplication.class, args);
		}
}

package ttd.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Configuration
class Step1Configuration {

		private final JdbcTemplate jdbcTemplate;
		private final StepBuilderFactory stepBuilderFactory;

		Step1Configuration(JdbcTemplate jdbcTemplate, StepBuilderFactory stepBuilderFactory) {
				this.jdbcTemplate = jdbcTemplate;
				this.stepBuilderFactory = stepBuilderFactory;
		}

		@Bean("step1")
		public Step step() {
				return this.stepBuilderFactory
					.get("step1")
					.tasklet((stepContribution, chunkContext) -> {
							jdbcTemplate.update(" update bookmark set publish_key = concat( date_trunc( 'day' , time) || '') ");
							return RepeatStatus.FINISHED;
					})
					.build();
		}
}
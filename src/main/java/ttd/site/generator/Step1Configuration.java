package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This step looks at every bookmark in the databases, assigns it a publish key
 * (basically, the year/month/day of the article, to support grouping).
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@RequiredArgsConstructor
class Step1Configuration {

	private final JdbcTemplate jdbcTemplate;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean("step1")
	public Step step() {
		return this.stepBuilderFactory.get("step1") //
				.tasklet((stepContribution, chunkContext) -> { //
					jdbcTemplate.update(" update bookmark set publish_key = concat( date_trunc( 'day' , time) || '') ");
					return RepeatStatus.FINISHED;
				})//
				.build();
	}

}
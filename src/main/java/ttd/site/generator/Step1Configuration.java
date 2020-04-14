package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This step looks at every bookmark in the databases, assigns it a publish key
 * (basically, the year/month/day of the article, to support grouping).
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@RequiredArgsConstructor
class Step1Configuration {

	private final static String STEP_NAME = "step1";

	private final StepBuilderFactory stepBuilderFactory;

	private final BookmarkDbService bookmarkDbService;

	@Bean(STEP_NAME)
	public Step step() {
		return this.stepBuilderFactory //
				.get(STEP_NAME) //
				.tasklet((stepContribution, chunkContext) -> { //
					var sql = " update bookmark set publish_key = concat( date_part('year', time) || '-' || lpad ('' || date_part('month', time) , 2, '0' )  || '-'|| lpad(  ''||date_part( 'day' , time)  , 2  ,'0') || '')  ";
					this.bookmarkDbService.template.update(sql);
					return RepeatStatus.FINISHED;
				})//
				.build();
	}

}
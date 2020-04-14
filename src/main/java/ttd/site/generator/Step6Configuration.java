package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.comparator.Comparators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Configuration
@Log4j2
@RequiredArgsConstructor
class Step6Configuration {

	private final static String STEP_NAME = "step6";

	private final StepBuilderFactory sbf;

	private final JdbcTemplate jdbcTemplate;

	private final SiteGeneratorConfigurationProperties properties;

	private final TemplateService templateService;

	@Bean(STEP_NAME)
	Step step() {
		return this.sbf //
				.get(STEP_NAME) //
				.tasklet((stepContribution, chunkContext) -> { //

					var newSql = """
									select 		distinct
																		date_part('year', time) || '-' ||  lpad(date_part('month', time)||'', 2, '0') as year_and_month,
																		date_part('year', time) as year,
																		date_part('month', time) as month
									from bookmark b
							""";
					var yearAndMonths = this.jdbcTemplate.query(newSql,
							(rs, i) -> new YearMonth(rs.getInt("year"), rs.getInt("month")));
					yearAndMonths.sort(Comparators.comparable());

					var indexHtml = templateService.index(yearAndMonths);

					var indexFile = new File(this.properties.getContentDirectory(), "index.html");
					try (var bw = new BufferedWriter(new FileWriter(indexFile))) {
						bw.write(indexHtml);
					}
					catch (IOException e) {
						ReflectionUtils.rethrowRuntimeException(e);
					}

					return RepeatStatus.FINISHED;
				})//
				.build();
	}

}

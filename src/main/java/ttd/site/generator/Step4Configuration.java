package ttd.site.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Need to generate the index page
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
class Step4Configuration {

	private final static String STEP_NAME = "step4";

	private final StepBuilderFactory sbf;

	private final JdbcTemplate jdbcTemplate;

	@Bean(STEP_NAME)
	Step step() {
		return this.sbf //
				.get(STEP_NAME) //
				.tasklet((stepContribution, chunkContext) -> { //
					// first break the years and months down
					this.jdbcTemplate.update("insert into bookmark_years_months (year, month, ym_key) "
							+ "select date_part('year', time), date_part('month', time), concat(date_part('year', time) || '-' || date_part('month', time)) from bookmark b "
							+ "on conflict on constraint bookmark_years_months_ym_key_key do nothing  ");
					// { years -> { months -> publish_keys }}
					// var yearsAndMonths = this.jdbcTemplate.query("select * from
					// bookmark_years_months bym ", (rs, i) -> new
					// YearMonth(rs.getInt("year"), rs.getInt("month")));
					// var model = new ConcurrentHashMap<Integer, Map<Integer,
					// Collection<String>>>();
					// yearsAndMonths.forEach(ym -> {
					// var m = ym.getMonth();
					// var y = ym.getYear();
					// model.putIfAbsent(y, new HashMap<>());
					// model.get(y).put(m, new ArrayList<>());

					// now load all the days for the year y and month m

					// });
					return RepeatStatus.FINISHED;
				}).build();
	}

}

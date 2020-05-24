package ttd.site.generator

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class Step4Configuration(
		private val sbf: StepBuilderFactory,
		private val jdbcTemplate: JdbcTemplate) {

	@Bean(STEP_NAME)
	fun step() =
			sbf //
					.get(STEP_NAME) //
					.tasklet { _: StepContribution?, _: ChunkContext? ->  //
						jdbcTemplate.update("""
							insert into bookmark_years_months (year, month, ym_key) 
									select date_part('year', time), date_part('month', time), concat(date_part('year', time) || '-' || date_part('month', time)) from bookmark b 
									 where b.deleted = false
									 on conflict on constraint bookmark_years_months_ym_key_key do nothing  """
						)
						RepeatStatus.FINISHED
					} //
					.build()


	companion object {
		private const val STEP_NAME = "step4"
	}
}
package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

@Configuration
class Step0Configuration(
		private val siteGenerationJobState: SiteGenerationJobState,
		private val templateService: TemplateService,
		private val jdbcTemplate: JdbcTemplate,
		private val templateProperties: TemplateServiceConfigurationProperties,
		private val siteGenerationProperties: SiteGeneratorConfigurationProperties,
		private val sbf: StepBuilderFactory) {

	private val log = LogFactory.getLog(javaClass)

	companion object {
		const val STEP_NAME = "step0"
	}

	@Bean(STEP_NAME)
	fun step(): Step {
		return sbf //
				.get(STEP_NAME) //
				.tasklet { _: StepContribution, context: ChunkContext ->
					val newSql =
							"""
							select distinct
								date_part('year', time) || '-' ||  lpad(date_part('month', time)||'', 2, '0') as year_and_month,
								date_part('year', time) as year,
								date_part('month', time) as month
							from bookmark b
					"""
					val yearAndMonths = this.jdbcTemplate.query(newSql) { rs, _ -> YearMonth(rs.getInt("year"), rs.getInt("month")) }
					yearAndMonths.sortWith(Comparator.naturalOrder())
					val html = templateService.years(yearAndMonths)
					val file = File(siteGenerationProperties.contentDirectory, "years.include")
					BufferedWriter(FileWriter(file)).use {
						it.write(html)
					}
					val yearMonth = yearAndMonths[0]
					siteGenerationJobState.latestYearMonth.set(yearMonth)
					log.info("storing the latest ${YearMonth::class.qualifiedName} as $yearMonth")
					RepeatStatus.FINISHED
				} //
				.build()
	}
}
package ttd.site.generator

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
    private val siteGenerationProperties: SiteGeneratorConfigurationProperties,
    private val sbf: StepBuilderFactory) {

  companion object {
    const val STEP_NAME = "step0"
  }

  @Bean(STEP_NAME)
  fun step(): Step {
    return sbf //
        .get(STEP_NAME) //
        .tasklet { _: StepContribution, _: ChunkContext ->
          val newSql =
              """
								select distinct
									date_part('year', time) || '-' || lpad(date_part('month', time)||'', 2, '0') as year_and_month,
									date_part('year', time) as year,
									date_part('month', time) as month
								from bookmark b
								where
								 b.deleted = false 
								and 
									b.edited is not null 
							"""
          val yearAndMonths = this.jdbcTemplate.query(newSql) { rs, _ -> YearMonth(rs.getInt("year"), rs.getInt("month")) }
          yearAndMonths.sortWith(Comparator.naturalOrder())
          val html = templateService.years(yearAndMonths)
          val file = File(siteGenerationProperties.contentDirectory.file, "years.include")
          BufferedWriter(FileWriter(file)).use {
            it.write(html)
          }
          val yearMonth = yearAndMonths[0]
          siteGenerationJobState.latestYearMonth.set(yearMonth)
          RepeatStatus.FINISHED
        } //
        .build()
  }
}
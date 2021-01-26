package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

@Configuration
class Step6Configuration(
		private val sbf: StepBuilderFactory,
		private val state: SiteGenerationJobState,
		private val properties: SiteGeneratorConfigurationProperties,
		private val templateService: TemplateService) {

	private val log = LogFactory.getLog(javaClass)

	@Bean(STEP_NAME)
	fun step(): Step {
		return sbf //
				.get(STEP_NAME) //
				.tasklet { _: StepContribution, _: ChunkContext ->

					log.info ( "step 6")
					val latestYearMonth: YearMonth = this.state.latestYearMonth.get()
					log.info("the latest is $latestYearMonth ")

					val indexHtml = this.templateService.index(latestYearMonth)
					BufferedWriter(FileWriter(File(this.properties.contentDirectory.file, "index.html"))).use {
						it.write(indexHtml)
					}
					RepeatStatus.FINISHED
				} //
				.build()
	}


	companion object {
		const val STEP_NAME = "step6"
	}
}
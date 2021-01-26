package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import javax.sql.DataSource

@Configuration
class Step2Configuration(
		private val sbf: StepBuilderFactory,
		private val jdbcTemplate: JdbcTemplate,
		private val dataSource: DataSource,
		private val templateService: TemplateService,
		private val properties: SiteGeneratorConfigurationProperties
) {

	private val log = LogFactory.getLog(javaClass)


	@Bean(STEP_NAME + "Reader")
	fun reader() =
			JdbcCursorItemReaderBuilder<String>() //
					.sql("select distinct( publish_key) from bookmark b where b.deleted = false order by publish_key") //
					.dataSource(dataSource) //
					.rowMapper { rs: ResultSet, _: Int -> rs.getString("publish_key") } //
					.name(javaClass.simpleName + "#reader") //
					.build()

	@Bean
	fun processor() = PublishKeyToBookmarksItemProcessor(jdbcTemplate)

	@Bean(STEP_NAME + "Writer")
	fun writer() = BlogItemWriter(templateService, properties)

	@Bean(STEP_NAME)
	fun step() = sbf[STEP_NAME] //
			.chunk<String, Map<String, Collection<Bookmark>>>(100) //
			.reader(reader()) //
			.processor(processor()) //
			.writer(writer()) //
			.build()

	companion object {
		private const val STEP_NAME = "step2"
	}
}
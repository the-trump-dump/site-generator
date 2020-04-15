package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

@Configuration
class Step5Configuration(
		private val siteGenerationJobState: SiteGenerationJobState,
		private val properties: SiteGeneratorConfigurationProperties,
		private val sbf: StepBuilderFactory,
		private val template: JdbcTemplate,
		private val dataSource: DataSource,
		private val templateService: TemplateService
) {

	private val log = LogFactory.getLog(javaClass)

	@Bean(STEP_NAME + "Reader")
	fun reader() =
			JdbcCursorItemReaderBuilder<YearMonth>() //
					.sql("select * from bookmark_years_months bym") //
					.dataSource(dataSource) //
					.rowMapper { rs: ResultSet, _: Int -> YearMonth(rs.getInt("year"), rs.getInt("month")) } //
					.name(javaClass.simpleName + "#reader") //
					.build()

	@Bean(STEP_NAME + "Processor")
	fun processor(): ItemProcessor<YearMonth, Bookmarks> {
		return ItemProcessor { ym: YearMonth ->
			val bookmarkList = template.query(
					"select * from bookmark b where date_part('month', time) = ? and date_part('year', time) = ? ", arrayOf<Any>(ym.month, ym.year), BookmarkRowMapper())
			Bookmarks(ym, bookmarkList)
		}
	}

	@Bean(STEP_NAME + "Writer")
	fun writer(): ItemWriter<Bookmarks> {
		return ItemWriter<Bookmarks> { items -> items.forEach { writeYearAndMonthBlog(it.yearMonth, it.bookmarks) } }
	}

	@Bean(STEP_NAME)
	fun step(): Step {
		return sbf //
				.get(STEP_NAME) //
				.chunk<YearMonth, Bookmarks>(100) //
				.reader(reader()) //
				.processor(processor()) //
				.writer(writer()) //
				.build()
	}

	private fun writeYearAndMonthBlog(ym: YearMonth, bookmarks: List<Bookmark>) {
		val yearMonthKey = ym.toString()
		val bookmarksByDate = mutableMapOf<String, MutableList<Link>>()
		val stringListFunction: (String) -> MutableList<Link> = { ArrayList() }
		bookmarks.forEach { (bookmarkId, _, description, _, _, href, _, _, time) ->
			val key = DateUtils.formatYearMonthDay(time)
			val link = Link(bookmarkId.toString(), href, description, time)
			bookmarksByDate.computeIfAbsent(key, stringListFunction).add(link)
		}

		write(File(properties.contentDirectory, "$yearMonthKey.html"), templateService.monthly(ym, bookmarksByDate))

		if (ym.toString() == siteGenerationJobState.latestYearMonth.get().toString()) {
			write(File(properties.contentDirectory, "${yearMonthKey}-latest.html"), templateService.monthlyWithoutFrame(ym, bookmarksByDate))
		}


	}

	private fun write(file: File, text: String) {
		BufferedWriter(FileWriter(file)).use { bw -> bw.write(text) }
	}

	companion object {
		private const val STEP_NAME = "step5"
	}
}
package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller
import org.springframework.batch.item.json.JsonFileItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import java.io.File
import javax.sql.DataSource

@Configuration
class Step3Configuration(
    private val siteGeneratorConfigurationProperties: SiteGeneratorConfigurationProperties,
    private val dataSource: DataSource,
    private val sbf: StepBuilderFactory
) {

    private val bookmarkRowMapper = BookmarkRowMapper()

    private val log = LogFactory.getLog(javaClass)

    @Bean(STEP_NAME + "Reader")
    fun reader() =
        JdbcCursorItemReaderBuilder<Bookmark>() //
            .sql("select * from bookmark where deleted = false order by publish_key") //
            .dataSource(dataSource) //
            .rowMapper(bookmarkRowMapper) //
            .name(javaClass.simpleName + "#reader") //
            .build()

    @Bean(STEP_NAME + "Writer")
    fun writer() =
        JsonFileItemWriter<Bookmark>(
            FileSystemResource(File(siteGeneratorConfigurationProperties.contentDirectory.file, "bookmarks.json")),
            JacksonJsonObjectMarshaller()
        )

    @Bean(STEP_NAME)
    fun step() = sbf[STEP_NAME] //
        .chunk<Bookmark, Bookmark>(1000) //
        .reader(reader()) //
        .writer(writer()) //
        .build()

    companion object {
        private const val STEP_NAME = "step3"
    }
}
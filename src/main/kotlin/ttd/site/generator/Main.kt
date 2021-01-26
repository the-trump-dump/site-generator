package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import java.time.Instant

@EnableBatchProcessing
@EnableConfigurationProperties(
    value = [
        TemplateServiceConfigurationProperties::class, SiteGeneratorConfigurationProperties::class]
)
@SpringBootApplication
class Main {

    private val log = LogFactory.getLog(javaClass)

    @Bean
    fun dbConnected(jdbc: JdbcTemplate) = ApplicationListener<ApplicationReadyEvent> {
        log.info("Starting the SiteGenerator..")
        val count: Int = jdbc.queryForObject("select count(*) from bookmark ")
        log.info("There are $count bookmarks to render as of ${Instant.now()}")
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

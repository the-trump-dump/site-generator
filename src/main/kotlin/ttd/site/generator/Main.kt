package ttd.site.generator

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableBatchProcessing
@EnableConfigurationProperties(value = [
	TemplateServiceConfigurationProperties::class, SiteGeneratorConfigurationProperties::class]
)
@SpringBootApplication
class Main

fun main(args: Array<String>) {
	runApplication<Main>(*args)
}

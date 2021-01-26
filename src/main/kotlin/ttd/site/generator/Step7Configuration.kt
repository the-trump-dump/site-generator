package ttd.site.generator

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.FileCopyUtils
import java.io.File

@Configuration
class Step7Configuration(
    private val sbf: StepBuilderFactory,
    private val properties: SiteGeneratorConfigurationProperties
) {

    private val log = LogFactory.getLog(javaClass)

    @Bean(STEP_NAME)
    fun step(): Step {
        return this.sbf //
            .get(STEP_NAME) //
            .tasklet { _: StepContribution, _: ChunkContext ->
                log.info("step 7")
                val dest = File(this.properties.contentDirectory.file, "static")
                if (!dest.exists()) {
                    dest.mkdirs()
                }
                properties.staticAssetsDirectory.file.listFiles()!!.forEach {
                    val destination = File(dest, it.name)
                    log.debug("copying ${it.absolutePath} to ${destination.absolutePath}")
                    FileCopyUtils.copy(it, destination)
                }
                RepeatStatus.FINISHED
            } //
            .build()
    }

    companion object {
        const val STEP_NAME = "step7"
    }

}
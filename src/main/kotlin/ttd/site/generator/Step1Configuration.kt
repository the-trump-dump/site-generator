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

@Configuration
class Step1Configuration(
    private val stepBuilderFactory: StepBuilderFactory,
    private val template: JdbcTemplate
) {

    private val log = LogFactory.getLog(javaClass)

    @Bean(STEP_NAME)
    fun step(): Step {
        return stepBuilderFactory //
            .get(STEP_NAME) //
            .tasklet { _: StepContribution?, _: ChunkContext? ->
                log.info("step 1")
                val sql =
                    " update bookmark set publish_key = concat( date_part('year', time) || '-' || lpad ('' || date_part('month', time) , 2, '0' )  || '-'|| lpad(  ''||date_part( 'day' , time)  , 2  ,'0') || '')  "
                template.update(sql)
                RepeatStatus.FINISHED
            } //
            .build()
    }

    companion object {
        private const val STEP_NAME = "step1"
    }
}
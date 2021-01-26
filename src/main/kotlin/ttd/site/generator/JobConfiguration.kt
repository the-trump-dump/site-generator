package ttd.site.generator

import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class JobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,  //
    private val s0: Step0Configuration,  //
    private val s1: Step1Configuration,  //
    private val s2: Step2Configuration,  //
    private val s3: Step3Configuration,  //
    private val s4: Step4Configuration,  //
    private val s5: Step5Configuration,  //
    private val s6: Step6Configuration,  //
    private val s7: Step7Configuration,  //
    private val ss: StepStopConfiguration
) {
    @Bean
    fun job(): Job {
        return jobBuilderFactory["blog-job"] //
            .start(s0.step()) //
            .next(s1.step()) //
            .next(s2.step()) //
            .next(s3.step()) //
            .next(s4.step()) //
            .next(s5.step()) //
            .next(s6.step()) //
            .next(s7.step()) //
            .next(ss.step()) //
            .incrementer(RunIdIncrementer()) //
            .build()
    }

}
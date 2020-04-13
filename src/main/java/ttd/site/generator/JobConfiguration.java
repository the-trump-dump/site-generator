package ttd.site.generator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableBatchProcessing
class JobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	private final Step1Configuration s1;

	private final Step2Configuration s2;

	private final Step3Configuration s3;

	private final Step5Configuration s5;

	JobConfiguration(JobBuilderFactory jobBuilderFactory, Step1Configuration s1, Step2Configuration s2,
			Step3Configuration s3, Step5Configuration s5) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s5 = s5;
	}

	@Bean
	Job job() {
		return this.jobBuilderFactory.get("blog-job") //
				.start(s1.step()) //
				.next(s2.step())//
				.next(s3.step())//
				.next(s5.step())//
				.incrementer(new RunIdIncrementer())//
				.build();
	}

}

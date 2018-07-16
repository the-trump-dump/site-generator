package ttd.batch;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import ttd.git.GitTemplate;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Configuration
class Step3Configuration {

		private final StepBuilderFactory sbf;
		private final GitTemplate gitTemplate;
		private final RoundupConfigurationProperties properties;

		Step3Configuration(StepBuilderFactory sbf, GitTemplate gitTemplate, RoundupConfigurationProperties properties) {
				this.sbf = sbf;
				this.gitTemplate = gitTemplate;
				this.properties = properties;
		}


		@Bean ("step3")
		Step step() {
				return this.sbf
					.get("step3")
					.tasklet((stepContribution, chunkContext) -> {
							this.gitTemplate.executeAndPush(g -> {
									File directory = properties.getContentDirectory();
									Files
										.walk(directory.toPath())
										.forEach(file -> {
												try {
														g.add().addFilepattern(file.toFile().getName()).call();
												}
												catch (GitAPIException e) {
														ReflectionUtils.rethrowRuntimeException(e);
												}
										});
									g.commit().setAll(true).setMessage("batch @ " +Instant.now().toString()).setAllowEmpty(true).call();

							});


							return RepeatStatus.FINISHED;
					})
					.build();
		}
}

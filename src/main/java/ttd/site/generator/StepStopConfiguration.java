package ttd.site.generator;

import com.joshlong.git.GitTemplate;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;

/**
 *
 * This step takes everything in the working directory, commits it, and then pushes it.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RequiredArgsConstructor
@Configuration
class StepStopConfiguration {

	private final static String STEP_NAME = "stepStop";

	private final StepBuilderFactory sbf;

	private final GitTemplate gitTemplate;

	private final SiteGeneratorConfigurationProperties properties;

	@Bean(STEP_NAME)
	Step step() {
		return this.sbf //
				.get(STEP_NAME) //
				.tasklet((stepContribution, chunkContext) -> { //
					this.gitTemplate.executeAndPush(git -> {
						File directory = properties.getContentDirectory();
						Files.walk(directory.toPath()).forEach(file -> {
							try {
								git.add().addFilepattern(file.toFile().getName()).call();
							}
							catch (GitAPIException e) {
								ReflectionUtils.rethrowRuntimeException(e);
							}
						});
						git.commit()//
								.setAll(true) //
								.setMessage("batch @ " + Instant.now().toString()) //
								.setAllowEmpty(true)//
								.call();
					});
					return RepeatStatus.FINISHED;
				})//
				.build();
	}

}

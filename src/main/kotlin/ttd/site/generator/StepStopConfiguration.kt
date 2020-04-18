package ttd.site.generator

import com.joshlong.git.GitTemplate
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ReflectionUtils
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

@Configuration
class StepStopConfiguration(
		private val sbf: StepBuilderFactory,
		private val gitTemplate: GitTemplate,
		private val properties: SiteGeneratorConfigurationProperties
) {

	@Bean(STEP_NAME)
	fun step(): Step {
		return sbf //
				.get(STEP_NAME) //
				.tasklet { _: StepContribution, _: ChunkContext ->  //

					if (properties.commit) {
						gitTemplate.executeAndPush { git: Git ->
							Files.walk(properties.contentDirectory.file.toPath()).forEach { file: Path ->
								try {
									git.add().addFilepattern(file.toFile().name).call()
								} catch (e: GitAPIException) {
									ReflectionUtils.rethrowRuntimeException(e)
								}
							}
							git.commit()
									.setAll(true)
									.setMessage("batch @ " + Instant.now().toString())
									.setAllowEmpty(true)
									.call()
						}
					}
					RepeatStatus.FINISHED
				}
				.build()
	}

	companion object {
		private const val STEP_NAME = "stepStop"
	}
}
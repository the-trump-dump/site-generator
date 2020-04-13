package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

/**
 * This step looks at all the pre-assigned publish_keys, loads all the bookmarks that have
 * the same publish key (which is the year/month/day), and then emits a file for that
 * particular day. The result is a working directory containing an .html file for everyday
 * for which there are bookmarks.
 * <p>
 * Some days may not have bookmarks, thus, no file results.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@RequiredArgsConstructor
class Step2Configuration {

	private final static String STEP_NAME = "step2";

	private final StepBuilderFactory sbf;

	private final JdbcTemplate jdbcTemplate;

	private final DataSource dataSource;

	private final TemplateService templateService;

	private final SiteGeneratorConfigurationProperties properties;

	@Bean(STEP_NAME + "Reader")
	JdbcCursorItemReader<String> reader() {
		return new JdbcCursorItemReaderBuilder<String>()//
				.sql("select distinct( publish_key) from bookmark order by publish_key")//
				.dataSource(this.dataSource)//
				.rowMapper((rs, rowNum) -> rs.getString("publish_key")) //
				.name(getClass().getSimpleName() + "#reader") //
				.build();
	}

	@Bean
	PublishKeyToBookmarksItemProcessor processor() {
		return new PublishKeyToBookmarksItemProcessor(this.jdbcTemplate);
	}

	@Bean(STEP_NAME + "Writer")
	BlogItemWriter writer() {
		return new BlogItemWriter(this.templateService, this.properties);
	}

	@Bean(STEP_NAME)
	Step step() {
		return this.sbf.get(STEP_NAME)//
				.<String, Map<String, Collection<Bookmark>>>chunk(100)//
				.reader(reader())//
				.processor(processor())//
				.writer(writer())//
				.build();
	}

}

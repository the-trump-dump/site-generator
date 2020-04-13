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
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Configuration
@RequiredArgsConstructor
class Step2Configuration {

	private final StepBuilderFactory sbf;
	private final JdbcTemplate jdbcTemplate;
	private final DataSource dataSource;
	private final TemplateService templateService;
	private final SiteGeneratorConfigurationProperties properties;

	@Bean
	JdbcCursorItemReader<String> reader() {
		return new JdbcCursorItemReaderBuilder<String>()//
			.sql("select distinct( publish_key ) from bookmark order by publish_key")//
			.dataSource(this.dataSource)//
			.rowMapper((rs, rowNum) -> rs.getString("publish_key")) //
			.name(getClass().getSimpleName() + "#reader") //
			.build();
	}

	@Bean
	PublishKeyToBookmarksItemProcessor processor() {
		return new PublishKeyToBookmarksItemProcessor(this.jdbcTemplate);
	}

	@Bean
	BlogItemWriter writer() {
		return new BlogItemWriter(this.templateService, this.properties);
	}

	@Bean("step2")
	Step step() {
		return this.sbf.get("step2")//
			.<String, Map<String, Collection<Bookmark>>>chunk(100)//
			.reader(reader())//
			.processor(processor())//
			.writer(writer())//
			.build();
	}

}

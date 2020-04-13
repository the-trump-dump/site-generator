package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.io.File;

/**
 * This step writes all the links into a giant `.json` file that a dynamic website could
 * use
 */
@Configuration
@RequiredArgsConstructor
public class Step3Configuration {

	private final static String STEP_NAME = "step3";

	private final DataSource dataSource;

	private final RowMapper<Bookmark> bookmarkRowMapper = new BookmarkRowMapper();

	private final SiteGeneratorConfigurationProperties siteGeneratorConfigurationProperties;

	private final StepBuilderFactory sbf;

	@Bean(STEP_NAME + "Reader")
	JdbcCursorItemReader<Bookmark> reader() {
		return new JdbcCursorItemReaderBuilder<Bookmark>()//
				.sql("select * from bookmark order by publish_key")//
				.dataSource(this.dataSource)//
				.rowMapper(this.bookmarkRowMapper) //
				.name(getClass().getSimpleName() + "#reader") //
				.build();
	}

	@Bean(STEP_NAME + "Writer")
	JsonFileItemWriter<Bookmark> writer() {
		var file = new File(this.siteGeneratorConfigurationProperties.getContentDirectory(), "bookmarks.json");
		var resource = new FileSystemResource(file);
		return new JsonFileItemWriter<>(resource, new JacksonJsonObjectMarshaller<>());
	}

	@Bean(STEP_NAME)
	Step step() {
		return this.sbf.get(STEP_NAME)//
				.<Bookmark, Bookmark>chunk(1000)//
				.reader(reader())//
				.writer(writer())//
				.build();
	}

}

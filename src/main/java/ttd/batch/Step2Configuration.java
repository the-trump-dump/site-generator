package ttd.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import ttd.templates.Link;
import ttd.templates.TemplateService;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Configuration
class Step2Configuration {

		private final StepBuilderFactory sbf;
		private final JdbcTemplate jdbcTemplate;
		private final DataSource dataSource;
		private final TemplateService templateService;
		private final RoundupConfigurationProperties properties;

		Step2Configuration(DataSource dataSource, JdbcTemplate jdbcTemplate, StepBuilderFactory sbf,
																					TemplateService ts,
																					RoundupConfigurationProperties properties) {
				this.properties = properties;
				this.dataSource = dataSource;
				this.jdbcTemplate = jdbcTemplate;
				this.templateService = ts;
				this.sbf = sbf;
		}

		@Bean
		JdbcCursorItemReader<String> reader() {
				return new JdbcCursorItemReaderBuilder<String>()
					.sql("select distinct( publish_key ) from bookmark order by publish_key")
					.dataSource(this.dataSource)
					.rowMapper((rs, rowNum) -> rs.getString("publish_key"))
					.name(getClass().getSimpleName() + "#reader")
					.build();
		}

		@Bean
		PublishKeyToBookmarksItemProcessor processor() {
				return new PublishKeyToBookmarksItemProcessor(this.jdbcTemplate);
		}

		@Bean
		BlogWriterItemWriter writer() {
				return new BlogWriterItemWriter(this.templateService,
					this.properties);
		}

		@Bean ("step2")
		Step step() {
				return this.sbf
					.get("step2")
					.<String, Map<String, Collection<Bookmark>>>chunk(100)
					.reader(reader())
					.processor(processor())
					.writer(writer())
					.build();
		}
}

class BlogWriterItemWriter implements ItemWriter<Map<String, Collection<Bookmark>>> {

		private final Log log = LogFactory.getLog(getClass());
		private final TemplateService templateService;
		private final RoundupConfigurationProperties roundupConfigurationProperties;

		BlogWriterItemWriter(TemplateService templateService, RoundupConfigurationProperties roundupConfigurationProperties) {
				this.roundupConfigurationProperties = roundupConfigurationProperties;
				this.templateService = templateService;
		}

		private Date fromPublishKey(String pk) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				TemporalAccessor temporalAccessor = formatter.parse(pk);
				LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
				ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
				Instant result = Instant.from(zonedDateTime);
				return new Date(result.toEpochMilli());
		}

		private String uniquePublishKey(String pk, Bookmark bookmark) {
				Assert.notNull(pk, "the publishKey argument must not be null");
				Assert.notNull(bookmark, "the bookmark must not be null");
				return /* pk + ":" +*/ Long.toString(bookmark.getBookmarkId());
		}

		private void writeBlog(String publishKey, Collection<Bookmark> bookmarks) {
				Date date = fromPublishKey(publishKey);
				String pk = publishKey.split(" ")[0].trim();
				File file = new File(this.roundupConfigurationProperties.getContentDirectory(), pk + ".html");

				Set<Link> links = bookmarks
					.stream()
					.map(bm -> new Link(uniquePublishKey(publishKey, bm), bm.getHref(), bm.getDescription(), bm.getTime()))
					.collect(Collectors.toSet());

				try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
						bw.write(this.templateService.daily(date, links));
				}
				catch (IOException e) {
						ReflectionUtils.rethrowRuntimeException(e);
				}
		}

		@Override
		public void write(List<? extends Map<String, Collection<Bookmark>>> list) throws Exception {
				list.forEach((map) -> map.forEach(BlogWriterItemWriter.this::writeBlog));
		}
}

class PublishKeyToBookmarksItemProcessor implements ItemProcessor<String, Map<String, Collection<Bookmark>>> {

		private final JdbcTemplate template;

		private Collection<String> arrayToCollection(java.sql.Array ts) {
				try {
						return new ArrayList<>(Arrays.asList((String[]) ts.getArray()));
				}
				catch (SQLException e) {
						throw new RuntimeException(e);
				}
		}

		private final RowMapper<Bookmark> rowMapper = (rs, rowNum) ->
			new Bookmark(
				rs.getLong("bookmark_id"),
				rs.getString("extended"),
				rs.getString("description"),
				rs.getString("meta"),
				rs.getString("hash"),
				rs.getString("href"),
				rs.getString("publish_key"),
				arrayToCollection(rs.getArray("tags")),
				rs.getTimestamp("time"));

		PublishKeyToBookmarksItemProcessor(JdbcTemplate template) {
				this.template = template;
		}

		@Override
		public Map<String, Collection<Bookmark>> process(String pk) {
				return Collections.singletonMap(pk, this.template.query(
					"select * from bookmark where publish_key = ? ", this.rowMapper, pk));
		}
}

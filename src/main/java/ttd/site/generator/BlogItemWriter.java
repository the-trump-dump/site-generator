package ttd.site.generator;

import org.springframework.batch.item.ItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
class BlogItemWriter implements ItemWriter<Map<String, Collection<Bookmark>>> {

	private final TemplateService templateService;

	private final SiteGeneratorConfigurationProperties siteGeneratorConfigurationProperties;

	BlogItemWriter(TemplateService templateService,
			SiteGeneratorConfigurationProperties siteGeneratorConfigurationProperties) {
		this.siteGeneratorConfigurationProperties = siteGeneratorConfigurationProperties;
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
		return /* pk + ":" + */ Long.toString(bookmark.getBookmarkId());
	}

	private void writeBlog(String publishKey, Collection<Bookmark> bookmarks) {
		Date date = fromPublishKey(publishKey);
		String pk = publishKey.split(" ")[0].trim();
		File file = new File(this.siteGeneratorConfigurationProperties.getContentDirectory(), pk + ".html");

		Set<Link> links = bookmarks.stream()
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
		list.forEach((map) -> map.forEach(BlogItemWriter.this::writeBlog));
	}

}

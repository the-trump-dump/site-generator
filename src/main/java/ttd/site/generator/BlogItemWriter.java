package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@RequiredArgsConstructor
class BlogItemWriter implements ItemWriter<Map<String, Collection<Bookmark>>> {

	private final TemplateService templateService;

	private final SiteGeneratorConfigurationProperties siteGeneratorConfigurationProperties;

	@SneakyThrows
	private Date fromPublishKey(String pk) {
		return new SimpleDateFormat("yyyy-MM-dd").parse(pk);
	}

	private String uniquePublishKey(String pk, Bookmark bookmark) {
		Assert.notNull(pk, "the publishKey argument must not be null");
		Assert.notNull(bookmark, "the bookmark must not be null");
		return Long.toString(bookmark.getBookmarkId());
	}

	private void writeBlog(String publishKey, Collection<Bookmark> bookmarks) {
		var date = fromPublishKey(publishKey);
		var pk = publishKey.trim();
		var file = new File(this.siteGeneratorConfigurationProperties.getContentDirectory(), pk + ".html");
		var links = bookmarks.stream()
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

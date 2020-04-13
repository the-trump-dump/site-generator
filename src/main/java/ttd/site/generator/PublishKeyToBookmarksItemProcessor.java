package ttd.site.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
class PublishKeyToBookmarksItemProcessor implements ItemProcessor<String, Map<String, Collection<Bookmark>>> {

	private final JdbcTemplate template;

	private final RowMapper<Bookmark> bookmarkRowMapper = new BookmarkRowMapper();

	@Override
	public Map<String, Collection<Bookmark>> process(String pk) {
		return Collections.singletonMap(pk,
				this.template.query("select * from bookmark where publish_key = ? ", this.bookmarkRowMapper, pk));
	}

}

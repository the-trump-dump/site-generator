package ttd.site.generator;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
import java.util.*;

class PublishKeyToBookmarksItemProcessor implements ItemProcessor<String, Map<String, Collection<Bookmark>>> {

	private final JdbcTemplate template;

	private final RowMapper<Bookmark> rowMapper = (rs, rowNum) -> new Bookmark(rs.getLong("bookmark_id"),
			rs.getString("extended"), rs.getString("description"), rs.getString("meta"), rs.getString("hash"),
			rs.getString("href"), rs.getString("publish_key"), arrayToCollection(rs.getArray("tags")),
			rs.getTimestamp("time"));

	PublishKeyToBookmarksItemProcessor(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public Map<String, Collection<Bookmark>> process(String pk) {
		return Collections.singletonMap(pk,
				this.template.query("select * from bookmark where publish_key = ? ", this.rowMapper, pk));
	}

	private Collection<String> arrayToCollection(java.sql.Array ts) {
		try {
			return new ArrayList<>(Arrays.asList((String[]) ts.getArray()));
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}

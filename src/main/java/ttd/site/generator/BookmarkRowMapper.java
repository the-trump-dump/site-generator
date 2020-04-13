package ttd.site.generator;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
class BookmarkRowMapper implements RowMapper<Bookmark> {

	@Override
	public Bookmark mapRow(ResultSet rs, int i) throws SQLException {
		return new Bookmark(rs.getLong("bookmark_id"), rs.getString("extended"), rs.getString("description"),
				rs.getString("meta"), rs.getString("hash"), rs.getString("href"), rs.getString("publish_key"),
				arrayToCollection(rs.getArray("tags")), rs.getTimestamp("time"));
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

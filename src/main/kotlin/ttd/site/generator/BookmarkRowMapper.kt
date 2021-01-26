package ttd.site.generator

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class BookmarkRowMapper : RowMapper<Bookmark> {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int) =
        Bookmark(
            rs.getLong("bookmark_id"), rs.getString("extended"), rs.getString("description"),
            rs.getString("meta"), rs.getString("hash"), rs.getString("href"), rs.getString("publish_key"),
            arrayToCollection(rs.getArray("tags")), rs.getTimestamp("time")
        )


    private fun arrayToCollection(ts: java.sql.Array) =
        try {
            ArrayList(Arrays.asList(*ts.array as Array<String>))
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
}
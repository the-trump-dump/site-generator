package ttd.site.generator

import org.springframework.batch.item.ItemProcessor
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*

class PublishKeyToBookmarksItemProcessor(private val template: JdbcTemplate) :
		ItemProcessor<String, Map<String, Collection<Bookmark>>> {

	private val bookmarkRowMapper = BookmarkRowMapper()

	override fun process(pk: String): Map<String, Collection<Bookmark>> =
			Collections.singletonMap(pk,
					template.query("select * from bookmark where publish_key = ? and deleted = false", this.bookmarkRowMapper, pk))

}
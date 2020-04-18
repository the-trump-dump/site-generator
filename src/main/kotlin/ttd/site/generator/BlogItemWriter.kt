package ttd.site.generator

import org.springframework.batch.item.ItemWriter
import org.springframework.util.ReflectionUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.stream.Collectors

class BlogItemWriter(
		private val templateService: TemplateService,
		private val properties: SiteGeneratorConfigurationProperties) : ItemWriter<Map<String, Collection<Bookmark>>> {

	private fun writeBlog(publishKey: String, bookmarks: Collection<Bookmark>) {
		val date = DateUtils.parseYearMonthDay(publishKey)
		val pk = publishKey.trim { it <= ' ' }
		val file = File(properties.contentDirectory.file, "$pk.html")
		val links = bookmarks.stream()
				.map { bm: Bookmark -> Link(bm.bookmarkId.toString(), bm.href, bm.description, bm.time) }
				.collect(Collectors.toSet())
		try {
			BufferedWriter(FileWriter(file)).use { bw -> bw.write(templateService.daily(date, links)) }
		} catch (e: IOException) {
			ReflectionUtils.rethrowRuntimeException(e)
		}
	}

	override fun write(items: List<Map<String, Collection<Bookmark>>>) {
		items.forEach { map ->
			map.forEach { (publishKey, bookmarks) ->
				writeBlog(publishKey, bookmarks)
			}
		}
	}
}
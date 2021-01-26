package ttd.site.generator

import java.util.*

data class Bookmark(
    val bookmarkId: Long,
    val extended: String,
    val description: String,
    val meta: String,
    val hash: String,
    val href: String,
    val publishKey: String,
    val tags: Collection<String>,
    val time: Date
)
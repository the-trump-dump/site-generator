package ttd.site.generator

class Bookmarks(val yearMonth: YearMonth, val bookmarks: MutableList<Bookmark>) {

	init {
		bookmarks.sortWith(Comparator { o1, o2 -> o1.time.compareTo(o2.time) })
	}
}
package ttd.site.generator;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
class Bookmarks {

	private final Comparator<Bookmark> comparator = Comparator.comparing(Bookmark::getTime).reversed();

	private final YearMonth yearMonth;

	private final List<Bookmark> bookmarks;

	Bookmarks(YearMonth yearMonth, List<Bookmark> bookmarks) {
		this.yearMonth = yearMonth;
		bookmarks.sort(comparator);
		this.bookmarks = bookmarks;
	}

}

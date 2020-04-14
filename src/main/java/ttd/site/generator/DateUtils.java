package ttd.site.generator;

import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class DateUtils {

	private static final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final Object monitor = new Object();

	public static String formatYearMonthDay(Date date) {
		synchronized (monitor) {
			return simpleDateFormat.format(date);
		}
	}

	@SneakyThrows
	public static Date parseYearMonthDay(String pk) {
		synchronized (monitor) {
			return simpleDateFormat.parse(pk);
		}
	}

}

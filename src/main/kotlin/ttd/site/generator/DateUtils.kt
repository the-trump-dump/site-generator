package ttd.site.generator

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val simpleDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val monitor = Object()

    @JvmStatic
    fun formatYearMonthDay(date: Date): String {
        synchronized(monitor) { return simpleDateFormat.format(date) }
    }

    @JvmStatic
    fun parseYearMonthDay(pk: String): Date {
        synchronized(monitor) { return simpleDateFormat.parse(pk) }
    }
}
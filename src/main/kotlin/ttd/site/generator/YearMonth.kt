package ttd.site.generator

import java.text.DateFormatSymbols
import java.util.*

class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {

    override fun compareTo(other: YearMonth): Int = other.toString().compareTo(toString())

    private fun lpad(text: String, noOfChars: Int, paddingChar: String): String {
        return if (text.length < noOfChars) {
            val delta = noOfChars - text.length
            paddingChar.repeat(delta) + text
        } else {
            text
        }
    }

    private fun getMonthAsTextByLocale(month: Int, locale: Locale = Locale.US) =
        DateFormatSymbols.getInstance(locale).months[month - 1]


    fun toMonthString() = getMonthAsTextByLocale(this.month)

    fun toMonthCommaYearString() = "${getMonthAsTextByLocale(this.month)}, ${this.year}"


    override fun toString(): String {
        val y = this.year
        val m = this.month
        val stringBuilder = StringBuilder()
        val yearAsString = y.toString()
        val monthAsString = m.toString()
        stringBuilder.append(if (y < 10) lpad(yearAsString, 2, "0") else yearAsString)
        stringBuilder.append('-')
        stringBuilder.append(if (m < 10) lpad(monthAsString, 2, "0") else monthAsString)
        return stringBuilder.toString()
    }
}
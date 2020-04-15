package ttd.site.generator

import java.util.*

interface TemplateService {
	fun monthly(yearMonth: YearMonth, links: Map<String, List<Link>>): String
	fun monthlyWithoutFrame(yearMonth: YearMonth, links: Map<String, List<Link>>): String
	fun years(yearAndMonths: List<YearMonth>): String
	fun index(latest: YearMonth): String
	fun daily(date: Date, links: Collection<Link>): String
}
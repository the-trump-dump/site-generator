package ttd.site.generator

import java.util.*

interface TemplateService {
	fun monthly(yearMonth: YearMonth, links: Map<String, List<Link>>): String
	fun index(yearAndMonths: List<YearMonth>): String
	fun daily(date: Date, links: Collection<Link>): String
}
package ttd.site.generator

import com.joshlong.templates.MarkdownService
import com.samskivert.mustache.Mustache
import org.commonmark.parser.Parser
import org.springframework.beans.BeanUtils
import org.springframework.core.io.Resource
import org.springframework.util.Assert
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.Comparator


open class MustacheTemplateService(
		private val fileNameResolver: (String) -> String,
		private val compiler: Mustache.Compiler,
		private val markdownService: MarkdownService,
		daily: Resource,
		index: Resource,
		monthly: Resource,
		frame: Resource,
		years: Resource,
		year: Resource,
		charset: Charset) : TemplateService {

	private fun createMustacheTemplateFromResource(resource: Resource) =
			InputStreamReader(resource.inputStream, this.charset)
					.use { this.compiler.compile(it) }

	private val charset = charset.name()
	private val daily = createMustacheTemplateFromResource(daily)
	private val index = createMustacheTemplateFromResource(index)
	private val monthly = createMustacheTemplateFromResource(monthly)
	private val _frame = createMustacheTemplateFromResource(frame)
	private val _years = createMustacheTemplateFromResource(years)
	private val _year = createMustacheTemplateFromResource(year)

	companion object {
		const val URL_MARKER = "_URL_"
		const val ID_MARKER = "_ID_"
		const val DESC_MARKER = "_DESC_"
	}

	override fun monthly(yearMonth: YearMonth, links: Map<String, List<Link>>): String {
		return this.frame(monthlyWithoutFrame(yearMonth, links))
	}

	override fun monthlyWithoutFrame(yearMonth: YearMonth, links: Map<String, List<Link>>): String {
		val newMap = mutableMapOf<String, List<Map<String, Any>>>()
		links.forEach { (k, v) ->
			newMap[k] = v.map { buildMapForLink(it) }
		}
		val dates = newMap.entries.sortedWith(Comparator { o1, o2 -> o1.key.compareTo(o2.key) }).reversed()
		val map = mapOf("yearAndMonth" to yearMonth, "dates" to dates)
		return this.monthly.execute(map)
	}

	override fun daily(date: Date, links: Collection<Link>): String {
		val context = mutableMapOf<String, Any>("links" to links.map(this::buildMapForLink))
		context["date"] = DateUtils.formatYearMonthDay(date)
		return this.frame(this.daily.execute(context))
	}

	override fun index(latest: YearMonth): String {
		val index = index.execute(mutableMapOf(
				"latest_date" to latest.toString(),
				"latest" to this.fileNameResolver("${latest}-latest.html")))
		return frame(index)
	}

	override fun years(yearMonths: List<YearMonth>): String {
		Assert.isTrue(yearMonths.isNotEmpty(), "there must be at least one element in the ${YearMonth::class.java.name} collection.")

		val yearToMonths = mutableMapOf<String, MutableList<YearMonth>>()
		yearMonths.forEach { yearToMonths.computeIfAbsent(it.year.toString() + "") { mutableListOf() }.add(it) }

		val sortedYears = ArrayList(yearToMonths.keys)
		sortedYears.sortWith(Comparator.naturalOrder())

		val htmlForEachYear = sortedYears
				.reversed()
				.map {
					val months = yearToMonths[it]!!
					months.sortedWith(java.util.Comparator { obj: YearMonth, other: YearMonth -> obj.compareTo(other) })
					_year.execute(mapOf("year" to it, "months" to months))
				}

		return this._years.execute(mapOf("years" to htmlForEachYear))
	}

	private fun frame(body: String) =
			this._frame.execute(mapOf("body" to body,
					"years" to fileNameResolver("years.include"),
					"built" to Instant.now().toString()))

	private fun buildMapForLink(lien: Link): Map<String, Any> =
			mutableMapOf<String, Any>()
					.apply {
						BeanUtils.getPropertyDescriptors(Link::class.java)
								.forEach { pd ->
									this[pd.name] = pd.readMethod.invoke(lien)
								}
						val template =
								if (listOf(URL_MARKER, ID_MARKER, DESC_MARKER)
												.firstOrNull { lien.description.contains(it) } != null) {
									lien.description
								} else "[_DESC_](_URL_)"
						this["html"] = buildHtml(template, lien.href, lien.publishKey, lien.description)
					}


	private fun buildHtml(template: String, href: String, pk: String, desc: String): String {
		val atomicReference = AtomicReference(template)
		mapOf(URL_MARKER to href, ID_MARKER to pk, DESC_MARKER to desc)
				.forEach { (k, v) -> atomicReference.set(atomicReference.get().replaceAll(k, v)) }
		val html = markdownService.convertMarkdownTemplateToHtml(atomicReference.get()).trim()
		return if (html.startsWith("<p>") && html.endsWith("</p>")) {
			html.substring(3, html.length - 4)
		} else {
			html
		}
	}
}

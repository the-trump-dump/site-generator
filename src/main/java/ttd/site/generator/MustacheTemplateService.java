package ttd.site.generator;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
class MustacheTemplateService implements TemplateService {

	private final String URL_MARKER = "_URL_";

	private final String ID_MARKER = "_ID_";

	private final String DESC_MARKER = "_DESC_";

	private final String charset;

	private final Mustache.Compiler compiler;

	private final Template daily, monthly, index, _list, _frame;

	private final PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(Link.class);

	private final Parser parser = Parser.builder().build();

	MustacheTemplateService(Mustache.Compiler compiler, Resource daily, Resource index, Resource monthly,
			Resource frame, Resource list, Charset charset) throws Exception {

		this.compiler = compiler;
		this.charset = charset.name();
		Assert.notNull(this.compiler, "the compiler should not be null");
		Assert.notNull(this.charset, "the charset should not be null");
		this.daily = createTemplate(daily);
		this.index = createTemplate(index);
		this.monthly = createTemplate(monthly);
		this._list = createTemplate(list);
		this._frame = createTemplate(frame);

		List.of(this.daily, this.index, this.monthly, this._list, this._frame)
				.forEach(t -> Assert.notNull(t, "the template should not be null"));
	}

	@Data
	@Log4j2
	@RequiredArgsConstructor
	static class KeyAndLinks {

		private final String key;

		private final List<Map<String, Object>> links;

	}

	private List<Map<String, Object>> linkMaps(List<Link> links) {
		return links.stream().map(this::buildMapForLink).collect(Collectors.toList());
	}

	private String frame(String body) {
		Map<String, Object> context = new HashMap<>();
		context.put("body", body);
		return this._frame.execute(context);
	}

	@Override
	public String monthly(YearMonth yearMonth, Map<String, List<Link>> links) {

		var listOfKeyAndLinks = links.entrySet().stream()//
				.map(entry -> new KeyAndLinks(entry.getKey(), linkMaps(entry.getValue())))//
				.sorted(Comparator.comparing(KeyAndLinks::getKey))//
				.collect(Collectors.toList());
		var map = new HashMap<String, Object>();
		map.put("yearAndMonth", yearMonth);
		map.put("dates", listOfKeyAndLinks);
		return this.frame(this.monthly.execute(map));
	}

	/**
	 * this renders a page that has the years and the months, as well as the latest
	 * month's worth of content.
	 */
	@Override
	public String index(List<YearMonth> yearAndMonths) {

		Assert.isTrue(yearAndMonths.size() > 0,
				"there must be at least one element in the " + YearMonth.class.getName() + " collection.");

		var latest = yearAndMonths.get(0);
		var file = latest.toString() + ".html";

		var yearToMonths = new HashMap<String, List<YearMonth>>();
		yearAndMonths.forEach(ym -> yearToMonths.computeIfAbsent(ym.getYear() + "", key -> new ArrayList<>()).add(ym));

		var sortedYears = new ArrayList<>(yearToMonths.keySet());
		sortedYears.sort(Comparator.naturalOrder());

		var htmlForEachYear = sortedYears // the goal here is to build up the HTML for
											// each year for the index
				.stream().map(year -> {
					var months = yearToMonths.get(year);
					months.sort(YearMonth::compareTo);
					return renderYearFragmentGiven(year, months);
				}) //
				.collect(Collectors.toList());

		var context = Map.of("years", htmlForEachYear, "latest", latest.toString());
		var index = this.index.execute(context);
		return frame(index);
	}

	private String renderYearFragmentGiven(String year, List<YearMonth> months) {
		// todo render the months
		// todo this should be moved into a mustache template
		String format = """
				 <div>
						<H1> %s </H1>
						<DIV> %s </DIV>
				</div>
					""".strip();
		Stream<String> stringStream = months.stream().map(ym -> String.format("""
				<a href="%s.html">%s</a>
				""", ym.toString(), ym.toString()));
		return String.format(format, year, stringStream.collect(Collectors.joining()));
	}

	private Map<String, Object> buildMapForLink(Link lien) {
		Map<String, Object> linkMapForRendering = new HashMap<>();
		// copy over attributes for javabean properties on the `Link` class.
		for (PropertyDescriptor pd : this.propertyDescriptors) {
			try {
				linkMapForRendering.put(pd.getName(), pd.getReadMethod().invoke(lien));
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// setup html attribute
		String inputDescription = lien.getDescription();
		String url = lien.getHref();
		String template = "[_DESC_](_URL_)";
		String publishKey = lien.getPublishKey();

		if (this.shouldProcessDescription(inputDescription)) {
			template = inputDescription;
		}

		String html = this.buildHtml(template, url, publishKey, inputDescription);

		linkMapForRendering.put("html", html);
		return linkMapForRendering;
	}

	@Override
	public String daily(Date date, Collection<Link> links) {
		var context = this.buildDefaultContextFor(links);
		context.put("date", DateUtils.formatYearMonthDay(date));
		return this.frame(this.daily.execute(context));
	}

	private Template createTemplate(Resource resource) throws IOException {
		try (var reader = new InputStreamReader(resource.getInputStream(), this.charset)) {
			return this.compiler.compile(reader);
		}
	}

	private String markdownToHtml(String input) {
		synchronized (this.parser) {
			Node document = parser.parse(input);
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			return renderer.render(document);
		}
	}

	private boolean shouldProcessDescription(String inputDescription) {
		String description = inputDescription + "";
		return Stream.of(URL_MARKER, ID_MARKER, DESC_MARKER).map(description::contains).filter(a -> a).findFirst()
				.orElse(false);
	}

	/* for testing */ String replace(String in, String find, String replace) {
		int start;
		while ((start = in.indexOf(find)) != -1) {
			String before = in.substring(0, start);
			String after = in.substring(start + find.length());
			in = before + replace + after;
		}
		return in;
	}

	private String buildHtml(String template, String href, String pk, String desc) {
		Map<String, String> replacements = new HashMap<>();
		replacements.put(URL_MARKER, href);
		replacements.put(ID_MARKER, pk);
		replacements.put(DESC_MARKER, desc);
		AtomicReference<String> ar = new AtomicReference<>();
		ar.set(template);
		replacements.forEach((k, v) -> ar.set(replace(ar.get(), k, v)));
		String html = markdownToHtml(ar.get()).trim();
		if (html.startsWith("<p>") && html.endsWith("</p>")) {
			html = html.substring("<p>".length());
			html = html.substring(0, html.lastIndexOf("</p>"));
		}
		return html;
	}

	private Map<String, Object> buildDefaultContextFor(Collection<Link> links) {
		Map<String, Object> ctx = new HashMap<>();
		ctx.put("links", links.stream().map(this::buildMapForLink).collect(Collectors.toList()));
		return ctx;
	}

}

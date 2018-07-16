package ttd.templates;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
class MustacheTemplateService implements TemplateService {

		private final String URL_MARKER = "_URL_";
		private final String ID_MARKER = "_ID_";
		private final String DESC_MARKER = "_DESC_";

		private final String charset;

		private final Mustache.Compiler compiler;

		private final Template daily, index;

		private final PropertyDescriptor[] propertyDescriptors =
			BeanUtils.getPropertyDescriptors(Link.class);

		private final Parser parser = Parser.builder().build();

		private final DateTimeFormatter formatter = DateTimeFormatter
			.ISO_LOCAL_DATE
			.withLocale(Locale.US)
			.withZone(ZoneId.of(ZoneId.SHORT_IDS.get("PST")));


		MustacheTemplateService(
			Mustache.Compiler compiler,
			Resource daily,
			Resource index,
			Charset charset) throws Exception {
				Assert.notNull(compiler, "the compiler must not be null");
				Assert.notNull(charset, "the charset must not be null");
				Assert.notNull(daily, "the daily template must not be null");
				Assert.notNull(index, "the index template must not be null");

				this.compiler = compiler;
				this.charset = charset.name();
				this.daily = createTemplate(daily);
				this.index = createTemplate(index);
		}

		@Override
		public String index(Collection<Link> links) {
				Map<String, Object> context = buildDefaultContextFor(links);
				return this.index.execute(context);
		}

		@Override
		public String daily(Date date, Collection<Link> links) {
				Map<String, Object> context = buildDefaultContextFor(links);
				context.put("date", dailyDate(date));
				return this.daily.execute(context);
		}

		private Template createTemplate(Resource resource) throws IOException {
				try (Reader reader = new InputStreamReader(resource.getInputStream(), this.charset)) {
						return this.compiler.compile(reader);
				}
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
				String template = "[_DESC_](_URL_)"; // "<a class=\"link\" href=\"_URL_\" name =\"_ID_\" id=\"_ID_\">_DESC_</a>";
				String publishKey = lien.getPublishKey();

				if (this.shouldProcessDescription(inputDescription)) {
						template = inputDescription;
				}

				String html = this.buildHtml(template, url, publishKey, inputDescription);

				linkMapForRendering.put("html", html);
				return linkMapForRendering;
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
				return Stream
					.of(URL_MARKER, ID_MARKER, DESC_MARKER)
					.map(description::contains)
					.filter(a -> a)
					.findFirst()
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

		private String dailyDate(Date date) {
				return this.formatter.format(date.toInstant());
		}
}
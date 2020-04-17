package ttd.site.generator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@ImportAutoConfiguration({ MustacheAutoConfiguration.class  })
@OverrideAutoConfiguration(enabled = false)
@RunWith(SpringRunner.class)
public class MustacheTemplateServiceTest {

	@Autowired
	private MustacheTemplateService ts;

	private Log log = LogFactory.getLog(getClass());

	private Link customLink = new Link("customLink", "http://adobe.com",
			"this is a <A href='_URL_'>interesting link</a> to _ID_.", new Date());

	@Before
	public void before() {
		Assert.assertNotNull("the template service must not be null!", this.ts);
	}

/*
	@Test
	public void replace() {
		String content = " <a class=\"link\" href=\"_URL_\" name =\"_ID_\" id=\"_ID_\">_DESC_</a>";
		String find = "_DESC_";
		String replace = "Treasury probing $25K Mnuchin flight from New York to DC | TheHill";
		String replaced = ts.replaceString(content, find, replace);
		Assert.assertEquals(replaced,
				" <a class=\"link\" href=\"_URL_\" name =\"_ID_\" id=\"_ID_\">Treasury probing $25K Mnuchin flight from New York to DC | TheHill</a>");
	}
*/

	@Test
	public void buildHtml ()  {

	}

	private List<Link> generateLinks(int count) {
		AtomicLong al = new AtomicLong();
		return Stream.generate(() -> {
			long ctr1 = al.incrementAndGet();
			return new Link("pk" + ctr1, "http://asite.com/" + ctr1, "description" + ctr1, new Date());
		}).limit(count).collect(Collectors.toList());
	}

	@Test
	public void index() {
		List<Link> results = generateLinks(5);
		results.add(customLink);
		String index = this.ts.index( (new YearMonth(10, 10)));
		Assert.assertNotNull(index);
		log.info(index);
	}

	@Test
	public void daily() {
		List<Link> results = generateLinks(5);
		results.add(customLink);

		String daily = this.ts.daily(new Date(), results);
		Assert.assertNotNull(daily);
		log.info(daily);
	}

}

@SpringBootApplication
class Application {

}
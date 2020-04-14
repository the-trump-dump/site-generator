package ttd.site.generator;

import org.junit.Assert;
import org.junit.Test;

public class YearMonthTest {

	private final YearMonth yearMonth = new YearMonth(2, 20);

	@Test
	public void testToString() throws Exception {
		var str = this.yearMonth.toString();
		Assert.assertEquals("02-20", str);
	}

}
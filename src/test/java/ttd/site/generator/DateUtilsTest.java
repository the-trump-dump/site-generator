package ttd.site.generator;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateUtilsTest {

	@Test
	public void dateUtils() {
		String inputStr = "2017-02-20";
		Date date = DateUtils.parseYearMonthDay(inputStr);
		String outputStr = DateUtils.formatYearMonthDay(date);
		Assert.assertEquals(outputStr, inputStr);
	}

}
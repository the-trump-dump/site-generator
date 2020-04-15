package ttd.site.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

@Data
@RequiredArgsConstructor
class YearMonth implements Comparable<YearMonth> {

	private final int year, month;

	@Override
	public String toString() {
		return yearMonthKey(this);
	}

	private static String lpad(String theText, int numberOfCharsDesiredTotal, String characterToUseAsPadding) {
		Assert.hasText(theText, "the text to pad must be non-null");
		if (theText.length() < numberOfCharsDesiredTotal) {
			var delta = numberOfCharsDesiredTotal - theText.length();
			return characterToUseAsPadding.repeat(delta) + theText;
		}
		else {
			return theText;
		}
	}

	private static String yearMonthKey(YearMonth ym) {
		var y = ym.getYear();
		var m = ym.getMonth();
		var stringBuilder = new StringBuilder();
		var yearAsString = Integer.toString(y);
		var monthAsString = Integer.toString(m);
		stringBuilder.append(y < 10 ? lpad(yearAsString, 2, "0") : yearAsString);
		stringBuilder.append('-');
		stringBuilder.append(m < 10 ? lpad(monthAsString, 2, "0") : monthAsString);
		return stringBuilder.toString();
	}

	@Override
	public int compareTo(YearMonth o) {
		if (o instanceof YearMonth ym) {
			return ym.toString().compareTo(toString());
		}
		return -1;
	}

}

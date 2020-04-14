package ttd.site.generator;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

interface TemplateService {

	String monthly(YearMonth yearMonth, Map<String, List<Link>> links);

	String index(Collection<Link> links);

	String daily(Date date, Collection<Link> links);

}

package ttd.site.generator;

import java.util.Collection;
import java.util.Date;

interface TemplateService {

	String index(Collection<Link> links);

	String daily(Date date, Collection<Link> links);

}

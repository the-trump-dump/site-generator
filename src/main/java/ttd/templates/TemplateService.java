package ttd.templates;

import java.util.Collection;
import java.util.Date;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
public interface TemplateService {

		String index(Collection<Link> links);

		String daily(Date date, Collection<Link> links);
}



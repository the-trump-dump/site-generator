package ttd.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {

		private String publishKey;
		private String href;
		private String description;
		private Date date;
}

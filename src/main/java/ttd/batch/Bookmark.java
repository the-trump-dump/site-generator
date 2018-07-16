package ttd.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {

		private Long bookmarkId;
		private String extended, description, meta, hash, href, publishKey;
		private Collection<String> tags = new ArrayList<>();
		private Date time;
}

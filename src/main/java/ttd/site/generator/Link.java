package ttd.site.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Link {

	private String publishKey;

	private String href;

	private String description;

	private Date date;

}

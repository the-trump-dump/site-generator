package ttd.batch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Data
@ConfigurationProperties("roundup")
public class RoundupConfigurationProperties {

		private File contentDirectory;

}

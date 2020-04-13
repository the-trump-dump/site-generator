package ttd.site.generator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Data
@ConfigurationProperties("site-generator")
public class SiteGeneratorConfigurationProperties {

	private File contentDirectory;

}

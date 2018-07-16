package ttd.templates;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.nio.charset.Charset;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Data
@ConfigurationProperties("blog.templates")
public class TemplateServiceConfigurationProperties {

		@Value("${classpath:/templates/daily.mustache}")
		private final Resource daily;

		@Value("${classpath:/templates/index.mustache}")
		private final Resource index;

		private final Charset charset = Charset.defaultCharset();
}

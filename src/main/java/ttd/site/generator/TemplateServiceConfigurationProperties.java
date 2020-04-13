package ttd.site.generator;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.nio.charset.Charset;

@Data
@ConfigurationProperties("blog.templates")
class TemplateServiceConfigurationProperties {

	@Value("classpath:/templates/daily.mustache")
	private Resource daily;

	@Value("classpath:/templates/index.mustache")
	private Resource index;

	private Charset charset = Charset.defaultCharset();

}

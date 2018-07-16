package ttd.templates;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Configuration
@AutoConfigureAfter(MustacheAutoConfiguration.class)
@EnableConfigurationProperties(TemplateServiceConfigurationProperties.class)
public class TemplateServiceAutoConfiguration {

		@Bean
		TemplateService templateService(Mustache.Compiler c, TemplateServiceConfigurationProperties tc) throws Exception {
				return new MustacheTemplateService(c, tc.getDaily(), tc.getIndex(), tc.getCharset());
		}
}

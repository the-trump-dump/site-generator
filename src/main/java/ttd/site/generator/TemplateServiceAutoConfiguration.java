package ttd.site.generator;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(MustacheAutoConfiguration.class)
@EnableConfigurationProperties(TemplateServiceConfigurationProperties.class)
class TemplateServiceAutoConfiguration {

	@Bean
	TemplateService templateService(Mustache.Compiler c, TemplateServiceConfigurationProperties tc) throws Exception {
		return new MustacheTemplateService(c, tc.getDaily(), tc.getIndex(), tc.getMonthly(), tc.getFrame(),
				tc.getList(), tc.getCharset());
	}

}

package ttd.site.generator

import com.samskivert.mustache.Mustache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TemplateServiceConfiguration {

	@Bean
	fun templateService(c: Mustache.Compiler, tc: TemplateServiceConfigurationProperties) =
			MustacheTemplateService(c, tc.daily, tc.index, tc.monthly, tc.frame, tc.list, tc.charset)

}
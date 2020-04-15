package ttd.site.generator

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import java.nio.charset.Charset

@ConfigurationProperties("blog.templates")
data class TemplateServiceConfigurationProperties(

		val charset: Charset = Charset.defaultCharset(),

		@Value("classpath:/templates/daily.mustache")
		val daily: Resource,

		@Value("classpath:/templates/index.mustache")
		val index: Resource,

		@Value("classpath:/templates/monthly.mustache")
		val monthly: Resource,

		@Value("classpath:/templates/_frame.mustache")
		val frame: Resource,

		@Value("classpath:/templates/_list-and-header.mustache")
		val list: Resource
)
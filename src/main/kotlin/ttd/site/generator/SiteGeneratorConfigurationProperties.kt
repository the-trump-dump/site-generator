package ttd.site.generator

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.io.File

@ConstructorBinding
@ConfigurationProperties("site-generator")
class SiteGeneratorConfigurationProperties(
		val contentDirectory: File,
		val commit: Boolean = true
//		val clone:Boolean = false
)
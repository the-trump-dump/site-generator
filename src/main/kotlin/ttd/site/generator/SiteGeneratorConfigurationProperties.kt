package ttd.site.generator

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource

@ConstructorBinding
@ConfigurationProperties("site-generator")
data class SiteGeneratorConfigurationProperties(
    val commit: Boolean = true,
    val contentDirectory: Resource,
    val staticAssetsDirectory: Resource
)
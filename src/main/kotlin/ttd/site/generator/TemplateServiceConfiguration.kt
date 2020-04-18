package ttd.site.generator

import com.joshlong.templates.MarkdownService
import com.samskivert.mustache.Mustache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

@Configuration
class TemplateServiceConfiguration {

	private fun read(file: File): String = BufferedReader(FileReader(file)).use { it.readText() }

	@Bean
	fun templateService(compiler: Mustache.Compiler,
	                    ms : MarkdownService ,
	                    sg: SiteGeneratorConfigurationProperties,
	                    tc: TemplateServiceConfigurationProperties) =
			MustacheTemplateService(
					{ read(File(sg.contentDirectory.file, it)) },
					compiler,
					ms,
					tc.daily,
					tc.index,
					tc.monthly,
					tc.frame,
//					tc.list,
					tc.years,
					tc.year,
					tc.charset
			)

}
package ttd.site.generator;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@Log4j2
@SpringBootApplication
@EnableConfigurationProperties(SiteGeneratorConfigurationProperties.class)
public class SiteGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiteGeneratorApplication.class, args);
	}
}

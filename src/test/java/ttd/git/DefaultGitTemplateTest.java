package ttd.git;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.function.Function;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/

@Log4j2
@ImportAutoConfiguration({GitTemplateAutoConfiguration.class})
@OverrideAutoConfiguration(enabled = false)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"blog.git.uri=file:///home/jlong/Desktop/ttd"})
public class DefaultGitTemplateTest {

		@Autowired
		private GitTemplate gs;

		@Autowired
		private GitTemplateConfigurationProperties properties;


		@Before
		public void setUp() {
				Assert.assertNotNull(GitTemplate.class.getName() + " must not be null", this.gs);
				log.info(this.properties.getLocalCloneDirectory().getAbsolutePath());
		}

		private boolean exists(File root, File match) {
				File[] files = root.listFiles(file -> file.getName().equalsIgnoreCase(match.getName()));
				boolean doesNotExist = (files == null || files.length == 0);
				return !doesNotExist;
		}


		@Test
		public void addFileToRepository() throws Exception {

				File tmp = Files.createTempFile("tmp", ".txt").toFile();
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
						bw.append("hello, world!");
				}
				File localCloneDirectory = this.properties.getLocalCloneDirectory();
				Function<File, Boolean> exists = file -> exists(localCloneDirectory, file);
				Assert.assertFalse(exists.apply(tmp));
				gs.executeAndPush(g -> {
						g.add().addFilepattern(tmp.getAbsolutePath()).call();
						g.commit().setMessage("adding " + tmp.getName()).call();
				});
				Assert.assertTrue(exists.apply(tmp));

		}
}
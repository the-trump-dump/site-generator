package ttd.git;

import org.eclipse.jgit.api.Git;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
public interface GitCallback {
		void execute(Git g) throws Exception;
}

package ttd.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
public interface PushCommandCreator {

		PushCommand createPushCommand(Git git) throws GitAPIException;

}

package ttd.git;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
public interface GitTemplate {

		void execute(GitCallback gitCallback);

		void executeAndPush(GitCallback callback);
}

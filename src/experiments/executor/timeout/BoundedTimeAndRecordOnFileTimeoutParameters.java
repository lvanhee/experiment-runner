package experiments.executor.timeout;

import java.nio.file.Path;
import java.util.function.Consumer;

import experiments.inout.ExperimentExportFunctions;
import experiments.model.Experiment;

public class BoundedTimeAndRecordOnFileTimeoutParameters implements TimeoutParameters {
	private final Path timeoutFilePath;
	private final long millis;
	private BoundedTimeAndRecordOnFileTimeoutParameters(Path timeoutFilePath, long millis) {
		this.timeoutFilePath = timeoutFilePath;
		this.millis = millis;
	}

	public static BoundedTimeAndRecordOnFileTimeoutParameters newInstance(Path timeoutFilePath, long millis) {
		return new BoundedTimeAndRecordOnFileTimeoutParameters(timeoutFilePath, millis);
	}

	@Override
	public long getMillis() {
		return millis;
	}

	@Override
	public Consumer<Experiment> getReactionOnTimeout() {
		return x->ExperimentExportFunctions.saveResultOnFile(x,timeoutFilePath);
	}

}

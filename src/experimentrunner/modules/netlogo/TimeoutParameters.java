package experimentrunner.modules.netlogo;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import experimentrunner.model.experiment.data.ExperimentSetup;

public interface TimeoutParameters {

	long getMillis();

	Consumer<ExperimentSetup> getReactionOnTimeout();
	
}

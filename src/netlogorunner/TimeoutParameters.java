package netlogorunner;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import experiments.model.Experiment;

public interface TimeoutParameters {

	long getMillis();

	Consumer<Experiment> getReactionOnTimeout();
	
}

package experiments.model.experimentRunner;

import java.util.function.Function;

import experiments.model.Experiment;
import experiments.model.ExperimentOutput;

@FunctionalInterface
public interface ExperimentRunner extends Function<Experiment, ExperimentOutput> {
}

package experimentrunner.model.experiment.data;

import java.util.Map;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;

public interface ExperimentOutput {

	Map<Variable, Value> getResultMap();

}

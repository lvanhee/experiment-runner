package experimentrunner.inout;

import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public interface MultiExperimentExecutor extends ExperimentRunner
{

	public static MultiExperimentExecutor parse(JSONObject jsonObject, ExperimentVariableNetwork vars) {
		String multiExperiment = (String)jsonObject.get("multi-experiment");
		if(multiExperiment.equals("full-reset-every-experiment"))
			return FullResetMultiExperimentExecutor.newInstance
					(()-> ExperimentRunner.parse(jsonObject, vars), true,true);

		throw new Error();
	}
	
	public Map<ExperimentSetup, ExperimentOutput> apply (Set<ExperimentSetup>s);
}

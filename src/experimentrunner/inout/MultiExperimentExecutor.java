package experimentrunner.inout;

import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;

import experimentrunner.inout.record.FileBaseDatabase;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public interface MultiExperimentExecutor extends ExperimentRunner
{

	public static MultiExperimentExecutor parse(JSONObject jsonObject, ExperimentVariableNetwork vars, FileBaseDatabase fileBaseDatabase) {
		String multiExperiment = (String)jsonObject.get("multi-experiment");
		if(multiExperiment.equals("parallel-executors"))
			return ParallelThreadMultiExperimentExecutor.newInstance
					(()-> ExperimentRunner.parse(jsonObject, vars,fileBaseDatabase), true,true, fileBaseDatabase);

		throw new Error();
	}
	
	public Map<ExperimentSetup, ExperimentOutput> apply (Set<ExperimentSetup>s);
}

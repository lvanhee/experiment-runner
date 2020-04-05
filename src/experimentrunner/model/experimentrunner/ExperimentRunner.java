package experimentrunner.model.experimentrunner;

import java.util.function.Function;

import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.modules.netlogo.NetLogoExperimentsRunner;

@FunctionalInterface
public interface ExperimentRunner extends Function<ExperimentSetup, ExperimentOutput> {
	public static ExperimentRunner parse(JSONObject jsonObject, ExperimentVariableNetwork vars) {
		if(jsonObject.get("type").equals("netlogo"))
			return NetlogoExperimentRunner.parse(jsonObject, vars);
		throw new Error();
	}
	
}

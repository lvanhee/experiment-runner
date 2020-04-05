package experimentrunner.inout;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import experimentrunner.inout.OutputGenerator.OutputType;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class FileOutput {

	public static void parse(JSONObject object, 
			ExperimentVariableNetwork vars, ExperimentRunner ee) {
		
	}

	private static void experimentAndSave(Path outputFolder, String string, JSONObject object, ExperimentRunner ee,
			ExperimentSetup es) {
		
		throw new Error();
	}
	
	
}

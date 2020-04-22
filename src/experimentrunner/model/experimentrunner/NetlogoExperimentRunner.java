package experimentrunner.model.experimentrunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import org.json.simple.JSONObject;

import experimentrunner.inout.record.FileBaseDatabase;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.modules.netlogo.NetlogoTicksIterableRunner;

public interface NetlogoExperimentRunner extends ExperimentRunner {

	public static ExperimentRunner parse(JSONObject jsonObject, ExperimentVariableNetwork vars, FileBaseDatabase fileBaseDatabase) {
		Path file = Paths.get((String)jsonObject.get("file"));
		String preSetup = "";
		if(jsonObject.containsKey("pre-setup"))
			preSetup = (String)jsonObject.get("pre-setup");
		if(vars.getInputVariables().contains(VariableImpl.newInstance("ticks")))
		{
			Variable withinSubjectVariable = VariableImpl.newInstance("ticks");

			
			Supplier<IterableExperimentRunner> newRunner =
					()->NetlogoTicksIterableRunner.newInstance(jsonObject, file, vars.getOutputVariables());
					
			Supplier<WithinSubjectIteratingExperimentsRunner> newIterating =
					()-> WithinSubjectIteratingExperimentsRunner.newInstance(
					vars, 
					newRunner.get(),
					withinSubjectVariable,
					vars.getRangeOf(VariableImpl.newInstance("ticks"))
					);
			
			 return ParallelWithinSubjectIteratingExperimentRunner.newInstance(
					 vars,
					 withinSubjectVariable,
					 newIterating, fileBaseDatabase
					 );
		}
		
		return SimpleNetLogoExperimentRunner.newInstance(file, vars.getOutputVariables(),preSetup);

		//throw new Error();
	}

}

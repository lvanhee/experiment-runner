package experimentrunner.model.experimentrunner;

import java.util.function.Supplier;

import org.json.simple.JSONObject;

import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.modules.netlogo.NetlogoTicksIterableRunner;

public interface NetlogoExperimentRunner {

	public static ExperimentRunner parse(JSONObject jsonObject, ExperimentVariableNetwork vars) {
		
		
		if(vars.getInputVariables().contains(VariableImpl.newInstance("ticks")))
		{
			Variable withinSubjectVariable = VariableImpl.newInstance("ticks");
			NetlogoTicksIterableRunner runner = 
					NetlogoTicksIterableRunner.newInstance(jsonObject, vars.getOutputVariables());
			
			Supplier<IterableExperimentRunner> newRunner =
					()->NetlogoTicksIterableRunner.newInstance(jsonObject, vars.getOutputVariables());
					
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
					 newIterating
					 );
		}
		throw new Error();
	}

}

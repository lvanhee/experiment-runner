package experimentrunner.model.experiment.data;


import java.util.Set;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class DataPointImpl implements DataPoint{
	private final ExperimentSetup experiment;
	private final ExperimentOutput output;
	
	public DataPointImpl(ExperimentSetup experiment, ExperimentOutput output) {
		this.experiment = experiment;
		this.output = output;
	}

	public static DataPointImpl parse(String x) {
		String start = x.substring(1, x.indexOf("}"));
		String end = x.substring(x.indexOf("{",1), x.length()-1);
		return DataPointImpl.newInstance(ExperimentSetupImpl.parseString(start),
				ExperimentOutputImpl.parse(end));
	}

	private static DataPointImpl newInstance(ExperimentSetup experiment, ExperimentOutputImpl output) {
		return new DataPointImpl(experiment, output);
	}

	@Override
	public ExperimentSetup getExperiment() {
		return experiment;
	}

	@Override
	public ExperimentOutput getExperimentOutput() {
		return output;
	}
	
	public String toString()
	{
		return experiment.toString()+":"+output.toString();
	}

	public static DataPoint newInstance(ExperimentSetup expe, ExperimentOutput out) {
		return new DataPointImpl(expe, out);
	}

	public static Set<DataPoint> from(ExperimentLinearScheduler bestSoFar, ExperimentRunner er) {
		return bestSoFar.getAllSetups().stream()
		.map(x->DataPointImpl.newInstance(x,er.apply(x)))
		.collect(Collectors.toSet());
	}

	@Override
	public Value getValueOf(Variable v) {
		if(experiment.getVariableAllocation().containsKey(v)
				&&output.getResultMap().containsKey(v))
			throw new Error("Variable both an input and output variable!");
		
		if(experiment.getVariableAllocation().containsKey(v))
			return experiment.getVariableAllocation().get(v);
		
		if(output.getResultMap().containsKey(v))
			return output.getResultMap().get(v);
		
		throw new Error("Asking to get the value of the undefined variable:"+v+" in "+this);
	}

}

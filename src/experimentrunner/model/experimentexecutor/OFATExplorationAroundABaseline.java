package experimentrunner.model.experimentexecutor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;

public class OFATExplorationAroundABaseline {
	
	private final ExperimentSetup baseline;
	private final Set<Variable> exploredVariables;
	private final Set<Variable> comparisonVariable;
	private final ExperimentVariableNetwork network;
	

	private OFATExplorationAroundABaseline(ExperimentSetup baseline2,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables,
			ExperimentVariableNetwork network) {
		this.baseline = baseline2;
		this.exploredVariables = exploredVariables2;
		this.comparisonVariable = comparisonVariables;
		this.network = network;
	}



	public static OFATExplorationAroundABaseline newInstance(
			ExperimentSetup baseline,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables,
			ExperimentVariableNetwork network
			) {
		return new OFATExplorationAroundABaseline(baseline,exploredVariables2, comparisonVariables, network);
	}



	public Set<Variable> getExploredVariables() {
		return exploredVariables;
	}



	public ExperimentLinearScheduler getSeriesAlteringTheValueOf(Variable v) {
		
		List<ExperimentSetup> res  =
				network.getRangeOf(v)
				.getValues()
				.stream()
				.map(x-> ExperimentSetup.getVariantReplacingTheValueOfAVariableBy(baseline, v, x))
				.collect(Collectors.toList());
		
		
		
		return ExperimentLinearScheduler.newInstance(res);
	}



	public Set<Variable> getLinesVariable() {
		return comparisonVariable;
	}



	public ExperimentSetup getBaseline() {
		return baseline;
	}

}

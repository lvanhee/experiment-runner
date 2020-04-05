package experimentrunner.model.experimentexecutor;

import java.util.Set;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;

public class OFATExplorationAroundABaseline {
	
	private final ExperimentSetup baseline;
	private final Set<Variable> exploredVariables;
	private final Set<Variable> comparisonVariable;
	
	

	private OFATExplorationAroundABaseline(ExperimentSetup baseline2,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables) {
		this.baseline = baseline2;
		this.exploredVariables = exploredVariables2;
		this.comparisonVariable = comparisonVariables;
	}



	public static OFATExplorationAroundABaseline newInstance(
			ExperimentSetup baseline,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables) {
		return new OFATExplorationAroundABaseline(baseline,exploredVariables2, comparisonVariables);
	}



	public Set<Variable> getExploredVariables() {
		return exploredVariables;
	}



	public ExperimentLinearScheduler getSeriesRelatedTo(Variable v) {
		Set<Variable> baselineVariable = baseline.getVariableAllocation()
				.keySet()
				.stream()
				.map(x->VariableImpl.newInstance(x, baseline.getVariableAllocation().get(x)))
				.filter(x->!x.getName().equals(v.getName())
						&& !comparisonVariable
								.stream()
								.anyMatch(y->y.getName().equals(x.getName())
								)
						)
				.collect(Collectors.toSet());
		baselineVariable.add(v);
		baselineVariable.addAll(comparisonVariable);
		return ExperimentLinearScheduler.newInstance(baselineVariable);
	}



	public Set<Variable> getLinesVariable() {
		return comparisonVariable;
	}



	public ExperimentSetup getBaseline() {
		return baseline;
	}

}

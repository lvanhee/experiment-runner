package experiments.model.explorationstrategies;

import java.util.Set;
import java.util.stream.Collectors;

import experiments.model.Experiment;
import experiments.model.ExperimentSeries;
import experiments.model.Variable;
import experiments.model.VariableImpl;

public class OFATExplorationAroundABaseline {
	
	private final Experiment baseline;
	private final Set<Variable> exploredVariables;
	private final Set<Variable> comparisonVariable;
	
	

	private OFATExplorationAroundABaseline(Experiment baseline2,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables) {
		this.baseline = baseline2;
		this.exploredVariables = exploredVariables2;
		this.comparisonVariable = comparisonVariables;
	}



	public static OFATExplorationAroundABaseline newInstance(
			Experiment baseline,
			Set<Variable> exploredVariables2, 
			Set<Variable> comparisonVariables) {
		return new OFATExplorationAroundABaseline(baseline,exploredVariables2, comparisonVariables);
	}



	public Set<Variable> getExploredVariables() {
		return exploredVariables;
	}



	public ExperimentSeries getSeriesRelatedTo(Variable v) {
		Set<Variable> baselineVariable = baseline.getInputMap()
				.keySet()
				.stream()
				.map(x->VariableImpl.newInstance(x, baseline.getInputMap().get(x)))
				.filter(x->!x.getName().equals(v.getName())
						&& !comparisonVariable
								.stream()
								.anyMatch(y->y.getName().equals(x.getName())
								)
						)
				.collect(Collectors.toSet());
		baselineVariable.add(v);
		baselineVariable.addAll(comparisonVariable);
		return ExperimentSeries.newInstance(baselineVariable);
	}



	public Set<Variable> getLinesVariable() {
		return comparisonVariable;
	}



	public Experiment getBaseline() {
		return baseline;
	}

}

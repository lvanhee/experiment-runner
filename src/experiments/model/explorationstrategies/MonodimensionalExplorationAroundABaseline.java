package experiments.model.explorationstrategies;

import java.util.Set;
import java.util.stream.Collectors;

import experiments.model.Experiment;
import experiments.model.ExperimentSeries;
import experiments.model.Variable;

public class MonodimensionalExplorationAroundABaseline {
	
	private final Experiment baseline;
	private final Set<Variable> exploredVariables;
	private final Variable comparisonVariable;
	
	

	private MonodimensionalExplorationAroundABaseline(Experiment baseline2, Set<Variable> exploredVariables2, 
			Variable comparisonVariable) {
		this.baseline = baseline2;
		this.exploredVariables = exploredVariables2;
		this.comparisonVariable = comparisonVariable;
	}



	public static MonodimensionalExplorationAroundABaseline newInstance(
			Experiment baseline,
			Set<Variable> exploredVariables, 
			Variable comparisonVariable) {
		return new MonodimensionalExplorationAroundABaseline(baseline,exploredVariables, comparisonVariable);
	}



	public Set<Variable> getExploredVariables() {
		return exploredVariables;
	}



	public ExperimentSeries getSeriesRelatedTo(Variable v) {
		Set<Variable> baselineVariable = baseline.getInputMap()
				.keySet().stream()
				.map(x->Variable.newInstance(x, baseline.getInputMap().get(x)))
				.filter(x->!x.getName().equals(v.getName())
						&& !x.getName().equals(comparisonVariable.getName())
						)
				.collect(Collectors.toSet());
		baselineVariable.add(v);
		baselineVariable.add(comparisonVariable);
		return ExperimentSeries.newInstance(baselineVariable);
	}



	public Variable getLineVariable() {
		return comparisonVariable;
	}



	public Experiment getBaseline() {
		return baseline;
	}

}

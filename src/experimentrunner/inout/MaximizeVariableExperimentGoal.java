package experimentrunner.inout;

import experimentrunner.inout.explore.ExplorationMode;
import experimentrunner.inout.model.ExperimentGoal;
import experimentrunner.model.experiment.variables.Variable;

public class MaximizeVariableExperimentGoal implements ExperimentGoal {
	
	private final Variable variableToOptimize;
	private final ExplorationMode em;
	private  MaximizeVariableExperimentGoal(Variable variableToOptimize, ExplorationMode em)
	{
		this.variableToOptimize = variableToOptimize;
		this.em = em;
	}

	public static ExperimentGoal newInstance(Variable variableToOptimize, ExplorationMode em) {
		return new MaximizeVariableExperimentGoal(variableToOptimize,em);
	}

	public ExplorationMode getExplorationMode() {
		return em;
	}

	public Variable getVariableToOptimizeAgainst() {
		return variableToOptimize;
	}

}

package experimentrunner.inout.model;

import org.json.simple.JSONObject;

import experimentrunner.inout.MaximizeVariableExperimentGoal;
import experimentrunner.inout.explore.ExplorationMode;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;

public interface ExperimentGoal {
	
	enum SimpleExperimentGoal implements ExperimentGoal{
		DRAW
	}
	
	enum ExperimentalGoalType{MAXIMIZE}

	public static ExperimentGoal parse(Object object) {
		if(object instanceof JSONObject)
		{
			JSONObject input = (JSONObject)object;
			String type = (String) input.get("type");
			ExperimentalGoalType egt = ExperimentalGoalType.valueOf(type.toUpperCase());
			if(egt==ExperimentalGoalType.MAXIMIZE)
			{
				Variable variableToOptimize = VariableImpl.newInstance((String) input.get("variable"));
				ExplorationMode em = ExplorationMode.parse(input.get("exploration-mode"));
				return MaximizeVariableExperimentGoal.newInstance(variableToOptimize,em);
			}
		}
		
		if(object instanceof String)
		{
			String input = (String)object;
			if(input.equals("draw"))
				return SimpleExperimentGoal.DRAW;
		}
		
		
		throw new Error();
	}

}

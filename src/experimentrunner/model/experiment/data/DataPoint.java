package experimentrunner.model.experiment.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;


public interface DataPoint {

	ExperimentSetup getExperiment();

	ExperimentOutput getExperimentOutput();

	static Map<Value, Set<Value>> toPoints(Set<DataPoint> set, Variable x, Variable y) {
		Map<Value, Set<Value>>res = new HashMap<Value, Set<Value>>();
		for(DataPoint d: set)
		{
			Value X =  d.getExperiment().getVariableAllocation().get(x);
			Value Y = d.getExperimentOutput().getResultMap().get(y);
			if(!res.containsKey(X))
				res.put(X, new HashSet<Value>());
			res.get(X).add(Y);
		}
		
		return res;
	}
	
	Value getValueOf(Variable sortBy);
}

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

	static Map<Object, Set<String>> toPoints(Set<DataPoint> set, String x, String y) {
		Map<Object, Set<String>>res = new HashMap<Object, Set<String>>();
		for(DataPoint d: set)
		{
			Value X =  d.getExperiment().getVariableAllocation().get(x);
			Value Y = d.getExperimentOutput().getResultMap().get(y);
			if(!res.containsKey(X))
				res.put(X, new HashSet<String>());
			res.get(X).add(Y);
		}
		
		return res;
	}
	
	Value getValueOf(Variable sortBy);
}

package experiments.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface DataPoint {

	Experiment getExperiment();

	ExperimentOutput getExperimentOutput();

	static Map<String, Set<String>> toPoints(Set<DataPoint> set, String x, String y) {
		Map<String, Set<String>>res = new HashMap<String, Set<String>>();
		for(DataPoint d: set)
		{
			String X =  d.getExperiment().getInputMap().get(x);
			String Y = d.getExperimentOutput().getResultMap().get(y);
			if(!res.containsKey(X))
				res.put(X, new HashSet<String>());
			res.get(X).add(Y);
		}
		
		return res;
	}

}

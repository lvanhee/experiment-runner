package experiments.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Experiment {
	public Map<String, String> getInputMap();

	public static Map<String, Set<DataPoint>> splitBy(Set<DataPoint> points, String lines) {
		Map<String, Set<DataPoint>>res = new HashMap<String, Set<DataPoint>>();
		for(DataPoint d : points)
		{
			if(!res.containsKey(d.getExperiment().getInputMap().get(lines)))
				res.put(d.getExperiment().getInputMap().get(lines), new HashSet<DataPoint>());
			res.get(d.getExperiment().getInputMap().get(lines)).add(d);
		}
		return res;
	}

	public static Experiment getTiltedVariant(Experiment setup, String parameter, String value) {
		Map<String, String> config = new HashMap<String, String>();
		config.putAll(setup.getInputMap());
		config.put(parameter, value);
		return ExperimentImpl.newInstance(config);
	}

}

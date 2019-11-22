package experiments.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Experiment {
	public Map<String, Object> getInputMap();

	public static Map<Object, Set<DataPoint>> splitBy(Set<DataPoint> points, String lines) {
		Map<Object, Set<DataPoint>>res = new HashMap<Object, Set<DataPoint>>();
		for(DataPoint d : points)
		{
			if(!res.containsKey(d.getExperiment().getInputMap().get(lines)))
				res.put(d.getExperiment().getInputMap().get(lines), new HashSet<DataPoint>());
			res.get(d.getExperiment().getInputMap().get(lines)).add(d);
		}
		return res;
	}

	public static Experiment getTiltedVariant(Experiment setup, String parameter,
			Object value) {
		Map<String, Object> config = new HashMap<String, Object>();
		config.putAll(setup.getInputMap());
		config.put(parameter, value);
		return ExperimentImpl.newInstance(config);
	}

}

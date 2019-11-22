package experiments.model;

import java.util.HashMap;
import java.util.Map;

import experiments.processing.ProcessingUtils;

public class ExperimentImpl implements Experiment {
	
	private final Map<String, Object> map;
	
	public ExperimentImpl(Map<String, Object> m) {
		if(m.keySet().stream().anyMatch(x->x.startsWith("obot")))
			System.out.println();
		this.map = m;
	}

	@Override
	public Map<String, Object> getInputMap() {
		return map;
	}

	public static ExperimentImpl newInstance(Map<String, Object> res) {
		return new ExperimentImpl(res);
	}
	
	public String toString()
	{
		return map.toString();
	}

	public static Experiment parseString(String input) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.putAll(ProcessingUtils.parseMap(input));
		return ExperimentImpl.newInstance(res);
	}

	public static Experiment merge(Experiment base, Experiment expand) {
		Map<String, Object> res = new HashMap<>();
		res.putAll(base.getInputMap());
		res.putAll(expand.getInputMap());
		return ExperimentImpl.newInstance(res);
	}
	
	public int hashCode()
	{
		return map.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return ((ExperimentImpl)o).map.equals(map);
	}

}

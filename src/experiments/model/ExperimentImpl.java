package experiments.model;

import java.util.HashMap;
import java.util.Map;

import experiments.processing.ProcessingUtils;

public class ExperimentImpl implements Experiment {
	
	private final Map<String, String> map;
	
	public ExperimentImpl(Map<String, String> m) {
		if(m.keySet().stream().anyMatch(x->x.startsWith("obot")))
			System.out.println();
		this.map = m;
	}

	@Override
	public Map<String, String> getInputMap() {
		return map;
	}

	public static ExperimentImpl newInstance(Map<String, String> res) {
		return new ExperimentImpl(res);
	}
	
	public String toString()
	{
		return map.toString();
	}

	public static Experiment parseString(String input) {
		return ExperimentImpl.newInstance(ProcessingUtils.parseMap(input));
	}

	public static Experiment merge(Experiment base, Experiment expand) {
		Map<String, String> res = new HashMap<>();
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

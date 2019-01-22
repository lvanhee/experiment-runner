package experiments.model;

import java.util.HashMap;
import java.util.Map;

import experiments.processing.ProcessingUtils;

public class ExperimentImpl implements Experiment {
	
	private final Map<String, String> map;
	
	public ExperimentImpl(Map<String, String> m) {
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

}

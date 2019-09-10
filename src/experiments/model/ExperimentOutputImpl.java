package experiments.model;

import java.util.Map;

import experiments.processing.ProcessingUtils;

public class ExperimentOutputImpl implements ExperimentOutput{
	
	private final Map<String, String> exp;
	
	private ExperimentOutputImpl(Map<String, String> m)
	{
		this.exp = m;
	}

	@Override
	public Map<String, String> getResultMap() {
		return exp;
	}
	
	public String toString()
	{
		return exp.toString();
	}

	public static ExperimentOutputImpl newInstance(Map<String, String> m) {
		return new ExperimentOutputImpl(m);
	}

	public static ExperimentOutputImpl parse(String end) {
		return newInstance(ProcessingUtils.parseMap(end));
	}
	
	public int hashCode()
	{
		return exp.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return ((ExperimentOutputImpl)o).exp.equals(exp);
	}

}

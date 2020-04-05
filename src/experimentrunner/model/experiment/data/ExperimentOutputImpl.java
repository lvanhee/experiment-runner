package experimentrunner.model.experiment.data;

import java.util.Map;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ProcessingUtils;

public class ExperimentOutputImpl implements ExperimentOutput{
	
	private final Map<Variable, Value> exp;
	
	private ExperimentOutputImpl(Map<Variable, Value> m)
	{
		this.exp = m;
	}

	@Override
	public Map<Variable, Value> getResultMap() {
		return exp;
	}
	
	public String toString()
	{
		return exp.toString();
	}

	public static ExperimentOutputImpl newInstance(Map<Variable, Value> m) {
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

package experimentrunner.model.experiment.data;

import java.util.HashMap;
import java.util.Map;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ProcessingUtils;

public class ExperimentSetupImpl implements ExperimentSetup {
	
	private final Map<Variable, Value> independentVariables;
	
	public ExperimentSetupImpl(Map<Variable, Value> m)
	{
		this.independentVariables = m;
	}

	@Override
	public Map<Variable, Value> getVariableAllocation() {
		return independentVariables;
	}

	public static ExperimentSetupImpl newInstance(Map<Variable, Value> res) {
		return new ExperimentSetupImpl(res);
	}
	
	public String toString()
	{
		return independentVariables.toString();
	}

	public static ExperimentSetup parseString(String input) {
		Map<String, Value> res = new HashMap<String, Value>();
		res.putAll(ProcessingUtils.parseMap(input));
		return ExperimentSetupImpl.newInstance(res);
	}

	public static ExperimentSetup merge(ExperimentSetup base, ExperimentSetup expand) {
		Map<Variable, Value> res = new HashMap<>();
		res.putAll(base.getVariableAllocation());
		res.putAll(expand.getVariableAllocation());
		return ExperimentSetupImpl.newInstance(res);
	}
	
	public int hashCode()
	{
		return independentVariables.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return ((ExperimentSetupImpl)o).independentVariables.equals(independentVariables);
	}

}

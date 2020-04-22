package experimentrunner.model.experiment.data;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;

public interface ExperimentSetup {
	public Map<Variable, Value> getVariableAllocation();

	public static ExperimentSetup getVariantReplacingTheValueOfAVariableBy(
			ExperimentSetup setup, 
			Variable variable,
			Value value) {
		Map<Variable, Value> variableAllocation = new HashMap<Variable, Value>();
		variableAllocation.putAll(setup.getVariableAllocation());
		variableAllocation.put(variable, value);
		return ExperimentSetupImpl.newInstance(variableAllocation);
	}

	public static boolean isAllEqualExceptFor(ExperimentSetup s, ExperimentSetup t, Variable var)
	{
		Map<Variable, Value> v1 = new HashMap<Variable, Value>();
		v1.putAll(s.getVariableAllocation());
		v1.remove(var);
		
		Map<Variable, Value> v2 = new HashMap<Variable, Value>();
		v2.putAll(t.getVariableAllocation());
		v2.remove(var);
		
		return v1.equals(v2);
		
		
	}

	public static ExperimentSetup getVariantRemovingAllNotIn(ExperimentSetup es, Set<Variable> s) {
		Map<Variable, Value> values = new HashMap<Variable, Value>();
		values.putAll(es.getVariableAllocation());
		for(Variable v:s)values.remove(v);
		return ExperimentSetupImpl.newInstance(values);
	}
	
}

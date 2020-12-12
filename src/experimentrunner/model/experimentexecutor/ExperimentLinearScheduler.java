package experimentrunner.model.experimentexecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.data.ExperimentSetupImpl;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;

public class ExperimentLinearScheduler {

	private final List<ExperimentSetup> expes ;

	private ExperimentLinearScheduler(List<ExperimentSetup> expes2) {
		this.expes = expes2;
	}

	@Deprecated
	public static ExperimentLinearScheduler newInstance(
			Map<Variable, List<Value>> possibleParameterValues) {
		Map<Variable, List<Value>> tmp = new HashMap<>();
		tmp.putAll(possibleParameterValues);
		return new ExperimentLinearScheduler(getAllPossibleInstances(tmp)
				 .stream().map(x->ExperimentSetupImpl.newInstance(x))
					.collect(Collectors.toList())
				);
	}

	private static List<Map<Variable, Value>> getAllPossibleInstances(
			Map<Variable, List<Value>> possibleParameterValues) {
		if(possibleParameterValues.isEmpty())
			return Arrays.asList(new HashMap<>());
		Variable current = possibleParameterValues.keySet().iterator().next();
		List<Value> values = possibleParameterValues.get(current);
		possibleParameterValues.remove(current);
		
		List<Map<Variable,Value>> possibleMaps = 
				getAllPossibleInstances(possibleParameterValues);
		
		List<Map<Variable, Value>> res = new ArrayList<>();
		
		for(Value possibleValue : values)
		{
			List<Map<Variable,Value>> copyPossibleMaps = new ArrayList<>();
			for(Map<Variable, Value> m : possibleMaps)
			{
				Map<Variable, Value> copy = new HashMap<>();
				copy.putAll(m);
				copyPossibleMaps.add(copy);
			}
			copyPossibleMaps.forEach(x->x.put(current,  possibleValue));
			res.addAll(copyPossibleMaps);
		}
		return res;
	}

	public List<ExperimentSetup> getAllSetups() {
		return expes;
	}

	public static ExperimentLinearScheduler newInstance(ExperimentLinearScheduler es, String parameter, String value) {
		/*List<ExperimentSetup>expes = new LinkedList<>();
		for(ExperimentSetup setup:es.getAllSetups())
		{
			Map<String, Object> m = setup.getVariableAllocation();
			m.put(parameter, value);
			
			expes.add(ExperimentSetupImpl.newInstance(m));
		}
		return new ExperimentLinearScheduler(expes);*/
		throw new Error();
	}

	public static ExperimentLinearScheduler newInstance(ExperimentVariableNetwork vars) {
		Map<Variable, List<Value>> res = new HashMap<>();
		for(Variable r: vars.getInputVariables())
			res.put(r, vars.getRangeOf(r).getValues());
		return ExperimentLinearScheduler.newInstance(res);
	}

	public static List<ExperimentLinearScheduler> cartesianProduct(ExperimentLinearScheduler expe, ExperimentLinearScheduler newInstance) {
		List<ExperimentLinearScheduler> res = new LinkedList<>();
		
		for(ExperimentSetup base:expe.getAllSetups())
		{
			List<ExperimentSetup> experiments = new LinkedList<>();
			for(ExperimentSetup expand:newInstance.getAllSetups())
			{
				experiments.add(ExperimentSetupImpl.merge(base, expand));
			}
			
			res.add(ExperimentLinearScheduler.newInstance(experiments));
			
		}
		return res;
	}

	public static ExperimentLinearScheduler newInstance(List<ExperimentSetup> experiments) {
		return new ExperimentLinearScheduler(experiments);
	}
	
	public String toString()
	{
		return expes.toString();
	}

	public Map<String, Object> getLockedVariablesMap() {
		/*Map<String, Object> res = new HashMap<String, Object>();
		Set<String> free = new HashSet<String>();
		
		for(ExperimentSetup e: expes)
			for(String s: e.getVariableAllocation().keySet())
				if(free.contains(s))continue;
				else if(res.containsKey(s))
					if(res.get(s).equals(e.getVariableAllocation().get(s)))continue;
					else {free.add(s); res.remove(s);}
				else
					res.put(s, e.getVariableAllocation().get(s));
		return res;*/
		
		throw new Error();
	}

}

package experimentrunner.mains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.data.ExperimentSetupImpl;
import experimentrunner.model.experiment.ranges.VariableRange;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;

public class ExperimentSetUtils {

	public static Map<ExperimentSetup, Set<ExperimentSetup>> 
	getMergedSetupBy(Set<ExperimentSetup> allSetups,
			Set<Variable> variablesMergedAround) 
	{
		if(allSetups.stream().anyMatch(x->!x.getVariableAllocation().keySet().containsAll(variablesMergedAround)))
			throw new Error("A variable to be merged by is not included in all experiments:"+variablesMergedAround);
		
		Map<ExperimentSetup, Set<ExperimentSetup>> res =
				new HashMap<ExperimentSetup, Set<ExperimentSetup>>();
		
		for(ExperimentSetup ex: allSetups)
		{
			ExperimentSetup sub = getSubSetup(ex, variablesMergedAround);
			if(!res.containsKey(sub))
				res.put(sub, new HashSet<ExperimentSetup>());
			res.get(sub).add(ex);
		}
		return res;
	}

	public static ExperimentSetup getSubSetup(ExperimentSetup ex, Set<Variable> s) {
		return ExperimentSetupImpl.newInstance(
				ex.getVariableAllocation().keySet().stream()
				.filter(x->s.contains(x))
				.collect(Collectors.toMap(Function.identity(), x->ex.getVariableAllocation().get(x)))
				);
	}

	public static Set<ExperimentSetup> getAllSetups(ExperimentVariableNetwork vars) {
		return ExperimentLinearScheduler.newInstance(vars).getAllSetups()
		.stream().collect(Collectors.toSet());

	}

	public static List<ExperimentSetup> getAllSetupsSortedByIterationsOf(ExperimentSetup t, Variable v,
			VariableRange r) {
		List<ExperimentSetup> res = 
				r.getValues().stream()
				.map(x->ExperimentSetup.getVariantReplacingTheValueOfAVariableBy(t, v,x))
				.collect(Collectors.toList());
		return res;
		
	}

	public static Map<ExperimentSetup, Set<ExperimentSetup>> getMergedSetupBy(Set<ExperimentSetup> allSetups, Variable variableToIterateOn) {
		return getMergedSetupBy(allSetups, Arrays.asList(variableToIterateOn).stream().collect(Collectors.toSet()));
	}

	public static Set<ExperimentSetup> getAllSetupsRemoving(Set<ExperimentSetup> allSetups,
			Variable variableToIterateOn) {
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(variableToIterateOn);
		return allSetups.stream()
				.map(x->ExperimentSetup.getVariantRemovingAllNotIn(x, vars))
				.collect(Collectors.toSet());
	}

}

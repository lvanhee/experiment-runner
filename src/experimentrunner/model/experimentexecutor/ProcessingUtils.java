package experimentrunner.model.experimentexecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class ProcessingUtils {

	public static Map<String, String> parseMap(String input) {
		Map<String, String> res = new HashMap<String, String>();
		input = input.replaceAll("\\}", "").replaceAll(" ", "").replaceAll("\\{", "");
		for(String s: input.substring(0, input.length()).split(","))
			res.put(s.split("=")[0].substring(0),s.split("=")[1]);
		return res;
	}

	public static Map<Object, Set<DataPoint>> splitByValuesOf(Set<DataPoint> points, String variable) {
		Map<Object, Set<DataPoint>>res = new HashMap<Object, Set<DataPoint>>();
		for(DataPoint d : points)
		{
			if(!res.containsKey(d.getExperiment().getVariableAllocation().get(variable)))
				res.put(d.getExperiment().getVariableAllocation().get(variable), new HashSet<DataPoint>());
			res.get(d.getExperiment().getVariableAllocation().get(variable)).add(d);
		}
		return res;
	}

	public static boolean isSimplerInstanceRejected(
			ExperimentSetup x,
			Set<ExperimentSetup> rejectedInstances,
			BiPredicate<ExperimentSetup, ExperimentSetup> isSimpler) {
		return rejectedInstances
				.stream()
				.anyMatch(y->isSimpler.test(y,x));			
	}

}

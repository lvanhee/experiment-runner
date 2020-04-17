package experimentrunner.model.experimentexecutor;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.ranges.VariableRange;
import experimentrunner.model.experiment.values.NumericValue;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;

public class ProcessingUtils {

	public static Map<Variable, Value> parseMap(String input) {
		Map<Variable, Value> res 
		= new HashMap<Variable, Value>();
		input = input.replaceAll("\\}", "").replaceAll(" ", "").replaceAll("\\{", "");
		for(String s: input.substring(0, input.length()).split(","))
			res.put(VariableImpl.newInstance(s.split("=")[0].substring(0)),
					Value.parse(s.split("=")[1]));
		return res;
	}

	public static Map<Value, Set<DataPoint>> splitByValuesOf(Set<DataPoint> points, Variable variable) {
		Map<Value, Set<DataPoint>>res = new HashMap<Value, Set<DataPoint>>();
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

	public static Set<ExperimentSetup> getVariantsAlongVariable(
			ExperimentSetup bestSoFar, Variable v, VariableRange vr) {
		Set<ExperimentSetup> res =
				vr.getValues().stream()
				.map(x->ExperimentSetup.getVariantReplacingTheValueOfAVariableBy(bestSoFar, v, x))
				.collect(Collectors.toSet());
		return res;
	}
	
	public static Set<Map<Variable, Value>> getAllPossibleJointAllocationsFor(
			ExperimentVariableNetwork network,
			Set<Variable> lines) {
		Map<Variable, VariableRange> valuesPerParameter =
				network.getRanges()
				.keySet()
				.stream()
				.filter(x->lines.contains(x))
				.collect(Collectors.toMap(
						Function.identity(), 
						x->(VariableRange)network.getRangeOf(x)));
				
		return getAllPossibleJointAllocationsFor(
				valuesPerParameter
				);
		/*	Variable v;
			v.g*/
		}
	
	public static Set<Map<Variable, Value>> 
	getAllPossibleJointAllocationsFor(
			Map<Variable, VariableRange> valuesPerParameter
			)
	{
		Set<Map<Variable, Value>> res = new HashSet<>();
		if(valuesPerParameter.isEmpty()) {
			res.add(new HashMap<>());
			return res;
		}
		
		Map<Variable, VariableRange> tmp = new HashMap<Variable, VariableRange>();
		tmp.putAll(valuesPerParameter);
		Variable current = tmp.keySet().iterator().next();
		
		Set<Value>currentValues = tmp.get(current).getValues().stream()
				.collect(Collectors.toSet());
		tmp.remove(current);
		for(Value val: currentValues)
		{
			 Set<Map<Variable, Value>>next =
					 getAllPossibleJointAllocationsFor(tmp);
			 next.stream().forEach(x->x.put(current, val)); 
			 res.addAll(next);
		}
		return res;
	}

	public static List<Point2D.Double> experimentsToPoints(
			Set<DataPoint> elements,
			Variable x, Variable y
			) {
		Map<Value, Set<Value>> pointsNames = DataPoint.toPoints(elements, x,y);
		Map<Double, Double> pointValues = 
				pointsNames.keySet().stream()
				.collect(
						Collectors.toMap(
								z->(double)NumericValue.toDouble(z),
								z->(double) pointsNames
								.get(z)
								.stream()
								.mapToDouble(t->(double)NumericValue.toDouble(t))
								.average().getAsDouble()));

		return pointValues.keySet().stream().sorted().map(
				z->
				new Point2D.Double(z, pointValues.get(z))).collect(Collectors.toList());
	}


}

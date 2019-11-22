package experiments.inout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import experiments.model.DataPoint;
import experiments.model.Variable;
import experiments.model.VariableImpl;

public class ToTikzPlot {
	
	private static Set<Map<String, Object>> getCartesianProduct(
			Set<String> input, 
			Set<DataPoint> points) {
		Map<String, Set<Object>>valuesPerParameter = 
				input.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						x-> points
						.stream()
						.map(y->y.getExperiment().getInputMap().get(x))
						.collect(Collectors.toSet())
						));
		return getCartesianProduct(valuesPerParameter);
	}
	

	public static void exportToTikz(Set<DataPoint> points, String xAxis, 
			String yAxis,
			Set<Variable> lines) {		
		Set<Map<String,Object>> allLineInstances = getCartesianProduct(lines);
		for(Map<String,Object> line : allLineInstances)
		{
			List<DataPoint> d = 
					points.stream()
					.filter(x->
					{
						for(String s: line.keySet())
							if(!x.getExperiment().getInputMap().get(s)
									.equals(line.get(s)))
								return false;
						return true;
					}
							)
					.collect(Collectors.toList());
			
					d.sort((x,y)->
			Double.compare(Double.parseDouble(
					x.getExperiment().getInputMap().get(xAxis).toString()),
					Double.parseDouble(
							y.getExperiment().getInputMap().get(xAxis).toString())));
			System.out.print("\\addplot[ % x="+xAxis+" y="+yAxis
					+ "\n" + 
					"color=red,\n" + 
					"mark=square,\n" + 
					"]"
					+ "coordinates{");
			for(DataPoint dp: d)
				System.out.print("("+
						dp.getExperiment().getInputMap().get(xAxis)+","+
						dp.getExperimentOutput().getResultMap().get(yAxis)+")");
			System.out.println("};");
			System.out.println("\\addlegendentry{"+toLatex(line.toString())+"}\n");
		}
	}
	


	private static Set<Map<String, Object>> getCartesianProduct(Set<Variable> lines) {
	/*	Variable v;
		v.g*/

		Map<String, Set<Object>> trans = 
				lines.stream()
				.collect(
						Collectors.toMap(
								(Variable x)->(String)x.getName(),
								x->x.getValues().stream().collect(Collectors.toSet())));
		return getCartesianProduct(trans);
	}


	private static Set<Map<String, Object>> getCartesianProduct(
			Map<String, Set<Object>> valuesPerParameter)
	{
		Set<Map<String, Object>> res = new HashSet<>();
		if(valuesPerParameter.isEmpty()) {
			res.add(new HashMap<>());
			return res;
		}
		
		Map<String, Set<Object>> tmp = new HashMap<String, Set<Object>>();
		tmp.putAll(valuesPerParameter);
		String current = tmp.keySet().iterator().next();
		
		Set<Object>currentValues = tmp.get(current);
		tmp.remove(current);
		for(Object val: currentValues)
		{
			 Set<Map<String, Object>>next = getCartesianProduct(tmp);
			 next.stream().forEach(x->x.put(current, val)); 
			 res.addAll(next);
		}
		return res;
	}
	
	private static String toLatex(String line) {
		return line.replaceAll("_", "");
	}





}

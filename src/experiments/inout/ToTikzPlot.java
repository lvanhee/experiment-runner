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

public class ToTikzPlot {
	
	private static Set<Map<String, String>> getCartesianProduct(
			Set<String> input, 
			Set<DataPoint> points) {
		Map<String, Set<String>>valuesPerParameter = 
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
			Variable lines) {		
		
		for(String line : lines.getValues())
		{
			List<DataPoint> d = 
					points.stream()
					.filter(x->
						x.getExperiment().getInputMap().get(lines.getName())
						.equals(line)
							)
					.collect(Collectors.toList());
			
					d.sort((x,y)->
			Double.compare(Double.parseDouble(x.getExperiment().getInputMap().get(xAxis)),
					Double.parseDouble(y.getExperiment().getInputMap().get(xAxis))));
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
			System.out.println("\\addlegendentry{"+toLatex(line)+"}\n");
		}
	}
	


	private static Set<Map<String, String>> getCartesianProduct(
			Map<String, Set<String>> valuesPerParameter)
	{
		Set<Map<String, String>> res = new HashSet<>();
		if(valuesPerParameter.isEmpty()) {
			res.add(new HashMap<>());
			return res;
		}
		
		Map<String, Set<String>> tmp = new HashMap<String, Set<String>>();
		tmp.putAll(valuesPerParameter);
		String current = tmp.keySet().iterator().next();
		
		Set<String>currentValues = tmp.get(current);
		tmp.remove(current);
		for(String val: currentValues)
		{
			 Set<Map<String, String>>next = getCartesianProduct(tmp);
			 next.stream().forEach(x->x.put(current, val)); 
			 res.addAll(next);
		}
		return res;
	}
	
	private static String toLatex(String line) {
		return line.replaceAll("_", "");
	}

}

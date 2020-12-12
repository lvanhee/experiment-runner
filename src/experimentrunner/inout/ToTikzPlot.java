package experimentrunner.inout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentexecutor.ProcessingUtils;

public class ToTikzPlot {
	
	/*private static Set<Map<String, Object>> getCartesianProduct(
			ExperimentVariableNetwork vars,
			Set<String> input, 
			Set<DataPoint> points) {
		Map<String, Set<Object>>valuesPerParameter = 
				input.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						x-> points
						.stream()
						.map(y->y.getExperiment().getVariableAllocation().get(x))
						.collect(Collectors.toSet())
						));
		return getCartesianProduct(vars,valuesPerParameter);
	}*/
	

	public static void exportToTikz(
			Set<DataPoint> points, Variable xAxis, 
			Variable yAxis,
			Set<Variable> lines,
			ExperimentVariableNetwork network) {
		if(! network.getInputVariables().contains(xAxis))
			throw new Error("Wrong variable name for the X axis");
		
		
		Set<Map<Variable,Value>> allLinesVariableAllocations = ProcessingUtils.getAllPossibleJointAllocationsFor
				(network,lines);
		for(Map<Variable,Value> line : allLinesVariableAllocations)
		{
			List<DataPoint> d = 
					points.stream()
					.filter(x->
					{
						for(Variable s: line.keySet())
							if(!x.getExperiment().getVariableAllocation().get(s)
									.equals(line.get(s)))
								return false;
						return true;
					}
							)
					.collect(Collectors.toList());
			
					d.sort((x,y)->
			Double.compare(Double.parseDouble(
					x.getExperiment().getVariableAllocation().get(xAxis).toString()),
					Double.parseDouble(
							y.getExperiment().getVariableAllocation().get(xAxis).toString())));
			System.out.print("\\addplot[ % x="+xAxis+" y="+yAxis
					+ "\n" + 
					"color=red,\n" + 
					"mark=square,\n" + 
					"]"
					+ "coordinates{");
			for(DataPoint dp: d)
				System.out.print("("+
						dp.getExperiment().getVariableAllocation().get(xAxis)+","+
						dp.getExperimentOutput().getResultMap().get(yAxis)+")");
			System.out.println("};");
			System.out.println("\\addlegendentry{"+toLatex(line.toString())+"}\n");
		}
	}
	

	
	
	private static String toLatex(String line) {
		return line.replaceAll("_", "");
	}


	static void exportToTikz(Set<DataPoint> points, Variable xAxis, Variable yAxis, Variable lines,
			ExperimentVariableNetwork network) {
		exportToTikz(
				points,
				xAxis,
				yAxis, 
				Arrays.asList(lines).stream().collect(Collectors.toSet()),
				network);
	}





}

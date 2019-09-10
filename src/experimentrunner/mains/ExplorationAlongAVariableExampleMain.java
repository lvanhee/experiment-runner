package experimentrunner.mains;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import experiments.inout.ToTikzPlot;
import experiments.model.DataPoint;
import experiments.model.DataPointImpl;
import experiments.model.Experiment;
import experiments.model.ExperimentImpl;
import experiments.model.ExperimentOutput;
import experiments.model.ExperimentOutputImpl;
import experiments.model.ExperimentSeries;
import experiments.model.Variable;
import experiments.model.experimentRunner.ExperimentRunner;
import experiments.model.explorationstrategies.MonodimensionalExplorationAroundABaseline;
import experiments.processing.ExperimentBatchRunner;

public class ExplorationAlongAVariableExampleMain {
	
	enum InputVariable{IV1, IV2, IV3, IV4}
	enum OutputVariable{OV1, OV2, OV3}
	
	
	enum Method{
		OPTIMAL,
		FULL_AUTO,PLAN}
	
	private final static List<String> methodNames = Arrays.asList(Method.values())
			.stream().map(x->x.toString()).collect(Collectors.toList());
	
	
		
	/**
	 * This main is a simple example for performing some exploration around a 
	 * default baseline alonw two variables.
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		
		
		Experiment baseline = getBaseline();
		Set<Variable> exploredVariables = new HashSet<Variable>();
		
		exploredVariables.add(
				Variable.newInstance(
						InputVariable.IV1.name(),
						0.01d, 0.1d, 1d)
				);
		
		exploredVariables.add(
				Variable.newInstance(
						InputVariable.IV3.name(),
						0.01d, 0.1d, 1d)
				);
		
		//This variable is the one used as a comparison criterion (e.g. it sets the
		//lines on a graph)
		Variable lineVariable = 
				Variable.newInstance(
						InputVariable.IV4.name(),
						Arrays.asList("a", "b","c","d")
				);
		
		MonodimensionalExplorationAroundABaseline explo = 
				MonodimensionalExplorationAroundABaseline
				.newInstance(baseline, 
						exploredVariables, 
						lineVariable);
		
		System.out.println("Baseline:"+explo.getBaseline());
		
		for(Variable v: explo.getExploredVariables())
		{
			ExperimentSeries es = explo.getSeriesRelatedTo(v);
			ExperimentRunner er = (x->
			{
				double o1 = Double.parseDouble(x.getInputMap().get(InputVariable.IV1.name()));
				double o2 = Double.parseDouble(x.getInputMap().get(InputVariable.IV2.name()));
				double o3 = Double.parseDouble(x.getInputMap().get(InputVariable.IV1.name()))+
						Double.parseDouble(x.getInputMap().get(InputVariable.IV3.name()));
				Map<String, String> res = new HashMap<String, String>();
				res.put(OutputVariable.OV1.name(),o1+"");
				res.put(OutputVariable.OV2.name(),o2+"");
				res.put(OutputVariable.OV3.name(),o3+"");
				return ExperimentOutputImpl.newInstance(res);
			});
			
			Set<DataPoint> values = ExperimentBatchRunner.process(es,er);

			for(OutputVariable ov:OutputVariable.values())
			{
				System.out.println("Experimenting "+v+","+ov+" regarding:"+explo.getLineVariable());
				ToTikzPlot.exportToTikz(values,
						v.getName(),
						ov.name(), 
						explo.getLineVariable());
			}
		}

		
		

	}
	
	private static Experiment getBaseline()
	{
		
		Map<String, String> m = new HashMap<String, String>();
		m.put(InputVariable.IV1.name(), 0.5+"");
		m.put(InputVariable.IV2.name(), 0.5+"");
		m.put(InputVariable.IV3.name(), 0.5+"");
		m.put(InputVariable.IV4.name(), 0.5+"");
		return ExperimentImpl.newInstance(m);
	}

}

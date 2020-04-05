package experimentrunner.mains;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import experimentrunner.inout.ToTikzPlot;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentOutputImpl;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.data.ExperimentSetupImpl;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentexecutor.ExperimentBatchRunner;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentexecutor.OFATExplorationAroundABaseline;
import experimentrunner.model.experimentrunner.ExperimentRunner;

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
		
		
		ExperimentSetup baseline = getBaseline();
		Set<Variable> exploredVariables = new HashSet<Variable>();
		
		exploredVariables.add(
				VariableImpl.newInstance(
						InputVariable.IV1.name(),
						0.01d, 0.1d, 1d)
				);
		
		exploredVariables.add(
				VariableImpl.newInstance(
						InputVariable.IV3.name(),
						0.01d, 0.1d, 1d)
				);
		
		//This variable is the one used as a comparison criterion (e.g. it sets the
		//lines on a graph)
		VariableImpl lineVariable = 
				VariableImpl.newInstance(
						InputVariable.IV4.name(),
						Arrays.asList("a", "b","c","d")
				);
		Set<Variable> linesVariable = new HashSet<Variable>();
		linesVariable.add(lineVariable);
		
		OFATExplorationAroundABaseline explo = 
				OFATExplorationAroundABaseline
				.newInstance(baseline, 
						exploredVariables, 
						linesVariable);
		
		System.out.println("Baseline:"+explo.getBaseline());
		
		for(Variable v: explo.getExploredVariables())
		{
			ExperimentLinearScheduler es = explo.getSeriesRelatedTo(v);
			ExperimentRunner er = (x->
			{
				double o1 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV1.name()).toString());
				double o2 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV2.name()).toString());
				double o3 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV1.name()).toString())+
						Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV3.name()).toString());
				Map<String, String> res = new HashMap<String, String>();
				res.put(OutputVariable.OV1.name(),o1+"");
				res.put(OutputVariable.OV2.name(),o2+"");
				res.put(OutputVariable.OV3.name(),o3+"");
				return ExperimentOutputImpl.newInstance(res);
			});
			
			Set<DataPoint> values = ExperimentBatchRunner.process(es,er);

			for(OutputVariable ov:OutputVariable.values())
			{
				System.out.println("Experimenting "+v+","+ov+" regarding:"+explo.getLinesVariable());
				ToTikzPlot.exportToTikz(values,
						v.getName(),
						ov.name(), 
						explo.getLinesVariable());
			}
		}

		
		

	}
	
	private static ExperimentSetup getBaseline()
	{
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put(InputVariable.IV1.name(), 0.5+"");
		m.put(InputVariable.IV2.name(), 0.5+"");
		m.put(InputVariable.IV3.name(), 0.5+"");
		m.put(InputVariable.IV4.name(), 0.5+"");
		return ExperimentSetupImpl.newInstance(m);
	}

}

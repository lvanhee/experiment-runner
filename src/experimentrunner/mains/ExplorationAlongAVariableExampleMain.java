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
import experimentrunner.model.experiment.ranges.DoubleVariableRange;
import experimentrunner.model.experiment.ranges.EnumVariableRange;
import experimentrunner.model.experiment.ranges.VariableRange;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentexecutor.ExperimentBatchRunner;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentexecutor.OFATExplorationAroundABaseline;
import experimentrunner.model.experimentrunner.ExperimentRunner;
import experimentrunner.model.experiment.values.*;

public class ExplorationAlongAVariableExampleMain {
	
	private enum InputVariable implements Variable{IV1, IV2, IV3, IV4;public String getName() {return toString();}}
	private enum OutputVariable implements Variable {OV1, OV2, OV3;
		public String getName() {return toString();}
		}
	
	
	enum Method{
		OPTIMAL,
		FULL_AUTO,PLAN}
	
	private final static List<String> methodNames = Arrays.asList(Method.values())
			.stream().map(x->x.toString()).collect(Collectors.toList());
	
	
		
	/**
	 * This main is a simple example for performing some exploration around a 
	 * default baseline along two variables.
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		
		
		ExperimentSetup baseline = getBaseline();
		Set<Variable> exploredVariables = new HashSet<Variable>();
		
		exploredVariables.addAll(
				Arrays.asList(
						InputVariable.IV1,
						InputVariable.IV3,
						InputVariable.IV4)
				);
				
		Set<Variable> linesVariable = new HashSet<Variable>();
		linesVariable.add(InputVariable.IV4);
		
		Map<Variable, VariableRange> rangePerInputVariable = new HashMap<>();
		rangePerInputVariable.put(InputVariable.IV1, DoubleVariableRange.newInstance(0.1, 0.1, 1));
		rangePerInputVariable.put(InputVariable.IV2, DoubleVariableRange.newInstance(0.5, 0.2, 1));
		rangePerInputVariable.put(InputVariable.IV3, DoubleVariableRange.newInstance(0.1, 0.3, 0.5));
		rangePerInputVariable.put(InputVariable.IV4, EnumVariableRange.newInstance(Arrays.asList("a","b","c")));
		
		Set<Variable> outputVariables = new HashSet<>();
		outputVariables.addAll(Arrays.asList(OutputVariable.values()));
		ExperimentVariableNetwork network = ExperimentVariableNetwork.newInstance(rangePerInputVariable, outputVariables);
		
		OFATExplorationAroundABaseline explo = 
				OFATExplorationAroundABaseline
				.newInstance(baseline, 
						exploredVariables, 
						linesVariable, network);
		
		System.out.println("Baseline:"+explo.getBaseline());
		
		
		for(Variable v: explo.getExploredVariables())
		{
			ExperimentLinearScheduler es = explo.getSeriesAlteringTheValueOf(v);
			ExperimentRunner er = (x->
			{
				double o1 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV1).toString());
				double o2 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV2).toString());
				double o3 = Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV1).toString())+
						Double.parseDouble(x.getVariableAllocation().get(InputVariable.IV3).toString());
				Map<Variable, Value> res = new HashMap<Variable, Value>();
				res.put(OutputVariable.OV1,DoubleValue.newInstance(o1));
				res.put(OutputVariable.OV2,DoubleValue.newInstance(o2));
				res.put(OutputVariable.OV3,DoubleValue.newInstance(o3));
				return ExperimentOutputImpl.newInstance(res);
			});
			
			Set<DataPoint> values = ExperimentBatchRunner.process(es,er);

			for(OutputVariable ov:OutputVariable.values())
			{
				System.out.println("Experimenting "+v+","+ov+" regarding:"+explo.getLinesVariable());
				ToTikzPlot.exportToTikz(values,
						v,
						ov, 
						explo.getLinesVariable(),
						network);
			}
		}

		
		

	}
	
	private static ExperimentSetup getBaseline()
	{
		
		Map<Variable, Value> m = new HashMap<Variable, Value>();
		m.put(InputVariable.IV1, DoubleValue.newInstance(0.5));
		m.put(InputVariable.IV2, DoubleValue.newInstance(0.5));
		m.put(InputVariable.IV3, DoubleValue.newInstance(0.5));
		m.put(InputVariable.IV4, DoubleValue.newInstance(0.5));
		return ExperimentSetupImpl.newInstance(m);
	}

}

package experiments.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import experiments.inout.FileReadWriter;

public class ExperimentSetup {
	
	private final Map<String, Set<String>> inputValuesPerParameter;
	private final Map<String, Set<String>> outputValuesPerParameter;
	
	public ExperimentSetup(Map<String, Set<String>> inputValuesPerParameter,
			Map<String, Set<String>> outputValuesPerParameter) {
		this.inputValuesPerParameter = inputValuesPerParameter;
		this.outputValuesPerParameter = outputValuesPerParameter;
	}

	public static ExperimentSetup parseSetup(Path inputFileName) {
		Set<DataPoint> datapoints = FileReadWriter.loadDataPoints(inputFileName);
		Set<String> inputSet = 
				datapoints.stream()
				.map(x->x.getExperiment().getInputMap().keySet())
				.reduce(new HashSet(), (x,y)->{x.addAll(y); return x;});
		
		Set<String> outputSet = 
				datapoints.stream()
				.map(x->x.getExperimentOutput().getResultMap().keySet())
				.reduce(new HashSet(), (x,y)->{x.addAll(y); return x;});
		
		Map<String, Set<String>> inputValuesPerParameter = new HashMap<>();
		
		for(String s:inputSet)
			inputValuesPerParameter.put(s, 
					datapoints.stream()
					.map(x->x.getExperiment().getInputMap().get(s))
					.collect(Collectors.toSet()));
		
		Map<String, Set<String>> outputValuesPerParameter = new HashMap<>();
		
		for(String s:outputSet)
			outputValuesPerParameter.put(
					s, 
					datapoints.stream()
					.map(x->x.getExperimentOutput().getResultMap().get(s))
					.collect(Collectors.toSet()));
			
		return new ExperimentSetup(inputValuesPerParameter, outputValuesPerParameter);
				
	}

	public boolean hasInputParameter(String s) {
		return inputValuesPerParameter.containsKey(s);
	}
}

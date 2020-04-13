package experimentrunner.model.experiment.variables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.data.ExperimentSetupImpl;
import experimentrunner.model.experiment.ranges.VariableRange;
import experimentrunner.model.experiment.values.Value;

public class ExperimentVariableNetwork {
	
	private final Map<Variable, VariableRange> rangeOfInputVariables;
	private final Set<Variable> outputVariables;
	
	private ExperimentVariableNetwork(Map<Variable, VariableRange> rangeOfInputVariables,
			Set<Variable> outputVariables)
	{
		this.rangeOfInputVariables = rangeOfInputVariables;
		this.outputVariables = outputVariables;
	}

	public static ExperimentVariableNetwork parse(JSONObject object) {
		Map<Variable, VariableRange> inputVariables = parseInputVariables(object);
		Set<Variable> outputVariables = new HashSet<>();	
		
		JSONArray array = (JSONArray) object.get("output-variables");
		for(Object o: array)
		{
			JSONObject va = (JSONObject) o;
			Variable v = VariableImpl.newInstance((String)va.get("name"));
			outputVariables.add(v);
		}
		
		return new ExperimentVariableNetwork(inputVariables, outputVariables);
	}

	private static Map<Variable, VariableRange> parseInputVariables(JSONObject object) {
		Map<Variable, VariableRange> inputVariables = new HashMap<Variable, VariableRange>();
		JSONArray array = (JSONArray) object.get("input-variables");
		for(Object o: array)
		{
			JSONObject va = (JSONObject) o;
			Variable v = VariableImpl.newInstance((String)va.get("name"));
			VariableRange range = VariableRange.parse((String)va.get("range"));
			inputVariables.put(v, range);
		}
		return inputVariables;
	}

	public Set<Variable> getOutputVariables() {
		return outputVariables;
	}

	public Set<Variable> getInputVariables() {
		return rangeOfInputVariables.keySet();
	}

	public VariableRange getRangeOf(Variable newInstance) {
		return rangeOfInputVariables.get(newInstance);
	}
	
	public String toString()
	{
		return rangeOfInputVariables+","+outputVariables;
	}

	public ExperimentSetup getRandomExperiment() {
		Map<Variable, Value> alloc =
				rangeOfInputVariables.keySet()
				.stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->rangeOfInputVariables.get(x).aRandomValue()));
		return ExperimentSetupImpl.newInstance(alloc);
	}

}

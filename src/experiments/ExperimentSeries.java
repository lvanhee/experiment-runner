package experiments;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentSeries {

	private final List<ExperimentSetup> expes ;

	public ExperimentSeries(List<ExperimentSetup> expes2) {
		this.expes = expes2;
	}

	public static ExperimentSeries newInstance(Map<String, List<String>> possibleParameterValues) {
		Map<String, List<String>> tmp = new HashMap<>();
		tmp.putAll(possibleParameterValues);
		return new ExperimentSeries(getAllPossibleInstances(tmp)
				 .stream().map(x->ExperimentSetup.newInstance(x))
					.collect(Collectors.toList())
				);
	}

	private static List<Map<String, String>> getAllPossibleInstances(
			Map<String, List<String>> possibleParameterValues) {
		if(possibleParameterValues.isEmpty())
			return Arrays.asList(new HashMap<>());
		String current = possibleParameterValues.keySet().iterator().next();
		List<String> values = possibleParameterValues.get(current);
		possibleParameterValues.remove(current);
		
		List<Map<String,String>> possibleMaps = getAllPossibleInstances(possibleParameterValues);
		
		List<Map<String, String>> res = new ArrayList<>();
		
		for(String possibleValue : values)
		{
			List<Map<String,String>> copyPossibleMaps = new ArrayList<>();
			for(Map<String, String> m : possibleMaps)
			{
				Map<String, String> copy = new HashMap<>();
				copy.putAll(m);
				copyPossibleMaps.add(copy);
			}
			copyPossibleMaps.forEach(x->x.put(current,  possibleValue));
			res.addAll(copyPossibleMaps);
		}
		return res;
	}

	public List<ExperimentSetup> getSetups() {
		return expes;
	}

	public static ExperimentSeries newInstance(ExperimentSeries es, String parameter, String value) {
		List<ExperimentSetup>expes = new LinkedList<>();
		for(ExperimentSetup setup:es.getSetups())
		{
			Map<String, String> m = setup.getParameters()
					.stream()
					.collect(Collectors.toMap(x->x, x->setup.getValueFor(x)));
			m.put(parameter, value);
			
			expes.add(ExperimentSetup.newInstance(m));
		}
		return new ExperimentSeries(expes);
	}

}

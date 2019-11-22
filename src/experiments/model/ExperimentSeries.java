package experiments.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExperimentSeries {

	private final List<Experiment> expes ;

	private ExperimentSeries(List<Experiment> expes2) {
		this.expes = expes2;
	}

	@Deprecated
	public static ExperimentSeries newInstance(
			Map<String, List<Object>> possibleParameterValues) {
		Map<String, List<Object>> tmp = new HashMap<>();
		tmp.putAll(possibleParameterValues);
		return new ExperimentSeries(getAllPossibleInstances(tmp)
				 .stream().map(x->ExperimentImpl.newInstance(x))
					.collect(Collectors.toList())
				);
	}

	private static List<Map<String, Object>> getAllPossibleInstances(
			Map<String, List<Object>> possibleParameterValues) {
		if(possibleParameterValues.isEmpty())
			return Arrays.asList(new HashMap<>());
		String current = possibleParameterValues.keySet().iterator().next();
		List<Object> values = possibleParameterValues.get(current);
		possibleParameterValues.remove(current);
		
		List<Map<String,Object>> possibleMaps = 
				getAllPossibleInstances(possibleParameterValues);
		
		List<Map<String, Object>> res = new ArrayList<>();
		
		for(Object possibleValue : values)
		{
			List<Map<String,Object>> copyPossibleMaps = new ArrayList<>();
			for(Map<String, Object> m : possibleMaps)
			{
				Map<String, Object> copy = new HashMap<>();
				copy.putAll(m);
				copyPossibleMaps.add(copy);
			}
			copyPossibleMaps.forEach(x->x.put(current,  possibleValue));
			res.addAll(copyPossibleMaps);
		}
		return res;
	}

	public List<Experiment> getSetups() {
		return expes;
	}

	public static ExperimentSeries newInstance(ExperimentSeries es, String parameter, String value) {
		List<Experiment>expes = new LinkedList<>();
		for(Experiment setup:es.getSetups())
		{
			Map<String, Object> m = setup.getInputMap();
			m.put(parameter, value);
			
			expes.add(ExperimentImpl.newInstance(m));
		}
		return new ExperimentSeries(expes);
	}

	public static ExperimentSeries newInstance(Set<Variable> ranges) {
		Map<String, List<Object>> res = new HashMap<>();
		for(Variable r: ranges)
			res.put(r.getName(), r.getValues());
		return ExperimentSeries.newInstance(res);
	}

	public static List<ExperimentSeries> cartesianProduct(ExperimentSeries expe, ExperimentSeries newInstance) {
		List<ExperimentSeries> res = new LinkedList<>();
		
		for(Experiment base:expe.getSetups())
		{
			List<Experiment> experiments = new LinkedList<>();
			for(Experiment expand:newInstance.getSetups())
			{
				experiments.add(ExperimentImpl.merge(base, expand));
			}
			
			res.add(ExperimentSeries.newInstance(experiments));
			
		}
		return res;
	}

	public static ExperimentSeries newInstance(List<Experiment> experiments) {
		return new ExperimentSeries(experiments);
	}
	
	public String toString()
	{
		return expes.toString();
	}

	public Map<String, Object> getLockedVariablesMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		Set<String> free = new HashSet<String>();
		
		for(Experiment e: expes)
			for(String s: e.getInputMap().keySet())
				if(free.contains(s))continue;
				else if(res.containsKey(s))
					if(res.get(s).equals(e.getInputMap().get(s)))continue;
					else {free.add(s); res.remove(s);}
				else
					res.put(s, e.getInputMap().get(s));
		return res;
	}

}

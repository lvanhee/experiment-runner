package experiments.inout;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import experiments.model.DataPoint;
import experiments.model.ExperimentSetup;


public class ToTikzPlotMain {
	
	private enum Keywords{INPUT_FILE, X_AXIS, Y_AXIS, CONSIDERED_DATA_POINTS, AVG_ON, LINES}

	public static void main(String[] args)
	{
		Path inputFileName = parseInputFileName(args);
		String xAxis = parseXAxis(args);
		String yAxis = parseYAxis(args);
		Set<String> lines = parseLines(args);
		Map<String, String> constraints = parseInputConstraints(args);
		Set<String> averageOn = parseAveragingOn(args);
		Set<DataPoint> points = getAllRelevantPoints(inputFileName, xAxis, yAxis, lines, constraints, averageOn);
		
		checkInput(points, yAxis);
		exportToTikz(points, xAxis, yAxis, lines);
	}

	private static void checkInput(Set<DataPoint> points, String yAxis) {
		if(!points.stream()
				.allMatch(x->x.getExperimentOutput().getResultMap().containsKey(yAxis)))
			throw new Error("Points do not contain the parameter:"+yAxis);
	}

	private static Set<String> parseLines(String[] args) {
		return FileReadWriter.parseSet(load(args, Keywords.LINES, true).get());
	}

	private static void exportToTikz(Set<DataPoint> points, String xAxis, 
			String yAxis,
			Set<String>lines) {
		
		Set<Map<String, String>> product = getCartesianProduct(lines, points);
		Set<Map<String, String>> sortedInput = new TreeSet<>(
				(x,y)->x.toString().compareTo(y.toString()));
		sortedInput.addAll(product);
		
		
		for(Map<String, String> line : sortedInput)
		{
			List<DataPoint> d = 
					points.stream()
					.filter(x->{
						for(String s: line.keySet())
							if(!x.getExperiment().getInputMap().get(s).equals(line.get(s)))
								return false;
						return true;
					}
							)
					.collect(Collectors.toList());
					d.sort((x,y)->
			Double.compare(Double.parseDouble(x.getExperiment().getInputMap().get(xAxis)),
					Double.parseDouble(y.getExperiment().getInputMap().get(xAxis))));
			System.out.print("\\addplot[\n" + 
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

	private static String toLatex(Map<String, String> line) {
		String res = "";
		for(String l: line.keySet())
			res+=l+"="+line.get(l)+",";
		res = res.replaceAll("_", "");
		return res;
	}

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

	private static Set<DataPoint> getAllRelevantPoints(Path inputFileName, 
			String xAxis, 
			String yAxis,
			Set<String> lines,
			Map<String, String> constraints, 
			Set<String> averageOn) {
		
		Set<DataPoint> points = FileReadWriter.loadDataPoints(inputFileName);
		ExperimentSetup es = ExperimentSetup.parseSetup(inputFileName);
		
		for(String s:constraints.keySet())
			if(!es.hasInputParameter(s))throw new Error("Constraint: "+s+" does not match an input parameter");
		
		for(String s: constraints.keySet())
			points = points.stream()
			.filter(x->x.getExperiment().getInputMap().get(s).equals(constraints.get(s)))
			.collect(Collectors.toSet());
		
		for(String s: averageOn)
			throw new Error();
		
		
		for(DataPoint dp: points)
		{
			for(String s: dp.getExperiment().getInputMap().keySet())
			{
				if(!constraints.containsKey(s)&& !(s.equals(xAxis) ||lines.contains(s)))
					throw new Error(dp+" has unconstrained parameter "+s);
			}
		}
		
		return points;
	}

	private static Set<String> parseAveragingOn(String[] args) {
		if(Arrays.asList(args)
				.stream()
				.filter(x->x.startsWith(Keywords.AVG_ON.toString())).findAny().isPresent())
			return toSetOfParameters(Arrays.asList(args)
				.stream()
				.filter(x->x.startsWith(Keywords.AVG_ON.toString()))
				.map(x->x.split(":")[1])
				.findAny().get());
		return new HashSet<>();
	}

	private static Set<String> toSetOfParameters(String next) {
		throw new Error();
	}

	private static String parseXAxis(String[] args) {
		return load(args, Keywords.X_AXIS, true).get();
	}

	
	private static String parseYAxis(String[] args) {
		return load(args, Keywords.Y_AXIS, true).get();
	}
	
	private static Map<String, String> parseInputConstraints(String[] args) {
		return toMapOfConstraints(load(args, Keywords.CONSIDERED_DATA_POINTS, true).get());
	}

	private static Map<String, String> toMapOfConstraints(String next) {
		return FileReadWriter.parseMap(next);
	}
	
	

	private static Path parseInputFileName(String[] args) {
		Optional<String> s = load(args, Keywords.INPUT_FILE, true);
		return Paths.get(s.get());
	}
	
	private static Optional<String> load(String[] args, Keywords k, boolean isMandatory) {
		Optional<String> res =
				Arrays.asList(args)
				.stream()
				.filter(x->x.startsWith(k.toString()))
				.map(x->x.split(":")[1])
				.findAny();
		
		if(isMandatory&& !res.isPresent())
			throw new Error("An input file name should have been given:"+k.toString());
		return res;
	}
}

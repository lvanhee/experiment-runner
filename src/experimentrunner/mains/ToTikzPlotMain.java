package experimentrunner.mains;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import experimentrunner.inout.FileReadWriter;
import experimentrunner.inout.ToTikzPlot;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;


public class ToTikzPlotMain {
	
	private enum Keywords{INPUT_FILE, X_AXIS, Y_AXIS, CONSIDERED_DATA_POINTS, AVG_ON, LINES}

	public static void main(String[] args)
	{
		/*Path inputFileName = parseInputFileName(args);
		Variable xAxis = parseXAxis(args);
		Variable yAxis = parseYAxis(args);
		Set<Variable> lines = parseLines(args);
		Map<String, String> constraints = parseInputConstraints(args);
		Set<String> averageOn = parseAveragingOn(args);
		Set<DataPoint> points = getAllRelevantPoints(inputFileName, xAxis, yAxis, lines, constraints, averageOn);
		
		checkInput(points, yAxis);
		ToTikzPlot.exportToTikz(points, xAxis, yAxis, lines);*/
		throw new Error();
	}

	private static void checkInput(Set<DataPoint> points, String yAxis) {
		if(!points.stream()
				.allMatch(x->x.getExperimentOutput().getResultMap().containsKey(yAxis)))
			throw new Error("Points do not contain the parameter:"+yAxis);
	}

	private static Set<String> parseLines(String[] args) {
		/*return FileReadWriter.parseSet(load(args, Keywords.LINES, true).get());*/
		throw new Error();
	}



	

	private static Set<DataPoint> getAllRelevantPoints(Path inputFileName, 
			String xAxis, 
			String yAxis,
			Set<String> lines,
			Map<String, String> constraints, 
			Set<String> averageOn) {
		
		Set<DataPoint> points = FileReadWriter.loadDataPoints(inputFileName);
		throw new Error();
		/*
		Experiment es = Experiment.parseSetup(inputFileName);
		
		for(String s:constraints.keySet())
			if(!es.getInputMap().containsKey(s))throw new Error("Constraint: "+s+" does not match an input parameter");
		
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
		
		return points;*/
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

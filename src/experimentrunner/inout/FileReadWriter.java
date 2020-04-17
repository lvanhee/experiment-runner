package experimentrunner.inout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.data.ExperimentSetupImpl;
import experimentrunner.model.experiment.values.ValueComparator;
import experimentrunner.model.experiment.variables.Variable;

public class FileReadWriter {
	
	public enum FileFormat{EXCEL, CSV, TIKZ,JFREECHART;

	public static FileFormat parse(String string) {
		return valueOf(string.toUpperCase());
	}}
	public enum FileOperation{REPLACE_ALL}


	private static final String EXCEL_SEPARATOR = "\t";
	private static final Comparator<Variable> DEFAULT_DISPLAY_ORDER = (x,y)->
		x.toString().compareTo(y.toString());
	

	public static Set<ExperimentSetup> loadExperiments(Path timeoutFilePath) 
	{
		Set<ExperimentSetup>res = new HashSet<ExperimentSetup>();
		try {
			if(Files.exists(timeoutFilePath)) {
				List<String> instances = Files.readAllLines(timeoutFilePath);
				for(String s: instances)
					res.add(ExperimentSetupImpl.parseString(s));
			}
	
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
		return res;
	}

	public static Set<DataPoint> loadDataPoints(String string) {
		Path p = Paths.get(string);
		return loadDataPoints(p);
	}

	public static void saveAs(
			Set<DataPoint> data, 
			Path p,
			FileFormat format,
			FileOperation fo,
			Optional<Variable> sortBy)
	{
		try {
			if(fo.equals(FileOperation.REPLACE_ALL))
				Files.deleteIfExists(p);
			
			if(!Files.exists(p))
			{
				if(data.isEmpty())return;
				Files.createFile(p);
				createHeader(p, data.iterator().next(), format, DEFAULT_DISPLAY_ORDER);
			}
			
			if(sortBy.isPresent())
			{
				Comparator<DataPoint> comp = 
						(x,y)->
							ValueComparator.INSTANCE.compare(
									x.getValueOf(sortBy.get()),
									y.getValueOf(sortBy.get()));
						
				Set<DataPoint> d = new TreeSet<DataPoint>(comp);
				d.addAll(data);
				data = d;
			}
			for(DataPoint dp: data)
				saveAs(dp, p,format, DEFAULT_DISPLAY_ORDER);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
		
	}

	private static void createHeader(Path p, DataPoint dp, FileFormat format, Comparator<Variable> variableDisplayOrder) {
		SortedSet<Variable>headerExperiment = new TreeSet<>(variableDisplayOrder);
		SortedSet<Variable>headerOutput= new TreeSet<>(variableDisplayOrder);
		headerExperiment.addAll(dp.getExperiment().getVariableAllocation().keySet());
		headerOutput.addAll(dp.getExperimentOutput().getResultMap().keySet());
		try {
			switch(format)
			{
			
			case EXCEL:
				Files.write(p,
						(
								headerExperiment.toString()
								.replaceAll("\\[", "")
								.replaceAll("\\]", "")
								.replaceAll(",", EXCEL_SEPARATOR)
								+EXCEL_SEPARATOR)
						.getBytes(),
						StandardOpenOption.APPEND);
				Files.write(p,
						(
								headerOutput.toString()
								.replaceAll("\\[", "")
								.replaceAll("\\]", "")
								.replaceAll(",", EXCEL_SEPARATOR)
								+"\n")
						.getBytes(),
						StandardOpenOption.APPEND);
				break;
			case CSV:
				Files.write(p,
						(
								headerExperiment.toString()
								.replaceAll("\\[", "")
								.replaceAll("\\]", "")
								.replaceAll(" ", "")
								+",")
						.getBytes(),
						StandardOpenOption.APPEND);
				Files.write(p,
						(
								headerOutput.toString()
								.replaceAll("\\[", "")
								.replaceAll("\\]", "")
								.replaceAll(" ", "")
								+"\n")
						.getBytes(),
						StandardOpenOption.APPEND);
				break;
			default: throw new Error();
			}
		}catch (IOException e) {
			throw new Error();
		}
	}


	public static void saveAs(DataPoint dp, Path p, FileFormat format, Comparator<Variable> displayOrder)
	{
		SortedSet<Variable>headerExperiment = new TreeSet<>(displayOrder);
		SortedSet<Variable>headerOutput= new TreeSet<>(displayOrder);
		headerExperiment.addAll(dp.getExperiment().getVariableAllocation().keySet());
		headerOutput.addAll(dp.getExperimentOutput().getResultMap().keySet());

		try {
			if(!Files.exists(p)) {
				Files.createFile(p);
				createHeader(p, dp, format, displayOrder);
			}
			
			String separator = "";
			switch(format)
			{
			case CSV: separator = ","; break;
			case EXCEL:separator = EXCEL_SEPARATOR; break;
			default: throw new Error();
			}

			for(Variable s: headerExperiment)
				Files.write(
						p,
						(dp.getExperiment().getVariableAllocation().get(s)+separator).getBytes(),
						StandardOpenOption.APPEND);

			for(Variable s: headerOutput)
				Files.write(
						p,
						(dp.getExperimentOutput().getResultMap().get(s)+separator).getBytes(),
						StandardOpenOption.APPEND);
			Files.write(
					p,
					"\n".getBytes(),
					StandardOpenOption.APPEND);

		}catch (IOException e) {
			throw new Error();
		}
	}
	
	public static void saveAs(DataPoint dp, Path p, FileFormat format)
	{
		saveAs(dp, p, format,DEFAULT_DISPLAY_ORDER);
	}

	
	public static Set<DataPoint> loadDataPoints(Path p) {
		try {
			if(Files.exists(p)) 
			{
				return Files.lines(p)
				.map((String x)->(DataPoint)(DataPointImpl.parse(x)))
				.collect(Collectors.toSet());
			}
			return new HashSet<>();
		}

		catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	public static Map<String, String> parseMap(String input) {
		if(input.equals("{}"))return new HashMap<>();
		Map<String, String> res = new HashMap<String, String>();
		input = input.replaceAll("}", "");
		for(String s: input.substring(0, input.length()).split(","))
		{
			if(s.startsWith("{")|| s.startsWith(" "))s = s.substring(1);
			res.put(s.split("=")[0],s.split("=")[1]);
		}
		return res;
	}

	public static Set<String> parseSet(String string) {
		assert(string.startsWith("{"));
		
		Set<String>res = new HashSet<>();
		res.addAll(
				Arrays.asList(
				string.substring(1, string.length()-1).split(",")
				));
		return res;
	
	}

}

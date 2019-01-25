package experiments.inout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import experiments.model.DataPoint;
import experiments.model.DataPointImpl;
import experiments.model.Experiment;
import experiments.model.ExperimentImpl;
import experiments.model.ExperimentOutput;

public class FileReadWriter {
	
	public enum FileFormat{EXCEL}
	public enum FileOperation{REPLACE_ALL}


	private static final String EXCEL_SEPARATOR = "\t";

	public static Set<Experiment> loadExperiments(Path timeoutFilePath) 
	{
		Set<Experiment>res = new HashSet<Experiment>();
		try {
			if(Files.exists(timeoutFilePath)) {
				List<String> instances = Files.readAllLines(timeoutFilePath);
				for(String s: instances)
					res.add(ExperimentImpl.parseString(s));
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

	public static void saveAs(Set<DataPoint> data, String filename,
			FileFormat format,
			FileOperation fo)
	{
		Path p = Paths.get(filename);
		try {
			if(fo.equals(FileOperation.REPLACE_ALL))
				Files.deleteIfExists(p);
			
			if(!Files.exists(p))
			{
				if(data.isEmpty())return;
				Files.createFile(p);
				createHeader(p, data.iterator().next(), format);
			}
			
			
			for(DataPoint dp: data)
				saveAs(dp, p,format);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
		
	}

	private static void createHeader(Path p, DataPoint dp, FileFormat format) {
		SortedSet<String>headerExperiment = new TreeSet<>();
		SortedSet<String>headerOutput= new TreeSet<>();
		headerExperiment.addAll(dp.getExperiment().getInputMap().keySet());
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
			default: throw new Error();
			}
		}catch (IOException e) {
			throw new Error();
		}
	}


	private static void saveAs(DataPoint dp, Path p, FileFormat format)
	{
		SortedSet<String>headerExperiment = new TreeSet<>();
		SortedSet<String>headerOutput= new TreeSet<>();
		headerExperiment.addAll(dp.getExperiment().getInputMap().keySet());
		headerOutput.addAll(dp.getExperimentOutput().getResultMap().keySet());

		try {
			for(String s: headerExperiment)
				Files.write(
						p,
						(dp.getExperiment().getInputMap().get(s)+EXCEL_SEPARATOR).getBytes(),
						StandardOpenOption.APPEND);

			for(String s: headerOutput)
				Files.write(
						p,
						(dp.getExperimentOutput().getResultMap().get(s)+EXCEL_SEPARATOR).getBytes(),
						StandardOpenOption.APPEND);
			Files.write(
					p,
					"\n".getBytes(),
					StandardOpenOption.APPEND);

		}catch (IOException e) {
			throw new Error();
		}
	}

	public static Set<DataPoint> loadDataPoints(Path p) {
		try {
			if(Files.exists(p)) 
				return Files.lines(p)
				.map((String x)->(DataPoint)(DataPointImpl.parse(x)))
				.collect(Collectors.toSet());
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

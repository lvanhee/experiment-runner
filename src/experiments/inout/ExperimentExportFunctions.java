package experiments.inout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import experiments.model.Experiment;
import experiments.model.ExperimentOutput;

public class ExperimentExportFunctions {

	public static void saveResultOnFile(
			Experiment es, 
			ExperimentOutput output, 
			Path p) {
		try {
			if(!Files.exists(p))
				Files.createFile(p);
			Files.write(p,
					(es.getInputMap().toString()+" "+output.getResultMap().toString()+"\n")
					.getBytes(),
					StandardOpenOption.APPEND);
		}catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	public static void saveResultOnFile(Experiment x, Path p) {
		try {
			if(!Files.exists(p))
				Files.createFile(p);
			Files.write(p,
					(x.getInputMap().toString()+"\n")
					.getBytes(),
					StandardOpenOption.APPEND);
		}catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

}

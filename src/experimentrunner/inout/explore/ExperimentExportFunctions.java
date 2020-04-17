package experimentrunner.inout.explore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class ExperimentExportFunctions {

	public static void saveResultOnFile(
			ExperimentSetup es, 
			ExperimentOutput output, 
			Path p) {
		try {
			if(!Files.exists(p))
				Files.createFile(p);
			Files.write(p,
					(es.getVariableAllocation().toString()+" "+output.getResultMap().toString()+"\n")
					.getBytes(),
					StandardOpenOption.APPEND);
		}catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	public static void saveResultOnFile(ExperimentSetup x, Path p) {
		try {
			if(!Files.exists(p))
				Files.createFile(p);
			Files.write(p,
					(x.getVariableAllocation().toString()+"\n")
					.getBytes(),
					StandardOpenOption.APPEND);
		}catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

}

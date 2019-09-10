package experiments.inout.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import experiments.inout.ExperimentExportFunctions;
import experiments.inout.FileReadWriter;
import experiments.model.DataPoint;
import experiments.model.DataPointImpl;
import experiments.model.Experiment;
import experiments.model.ExperimentOutput;

public class FileBaseDatabase {
	
	private final Path dataBaseLocation;
	private int REFRESH_PERIOD = 100;
	private int refreshCounter = REFRESH_PERIOD;
	private Set<DataPoint> results = null;

	
	private FileBaseDatabase(Path dataBaseLocation)
	{
		this.dataBaseLocation = dataBaseLocation;
	}
	
	public boolean hasAlreadyBeenProcessed(
			Experiment es) {
		if(results == null)
			results = FileReadWriter.loadDataPoints(dataBaseLocation);

		List<DataPoint> matchingOccurrencesForTheCurrentFile = 
				results
				.stream()
				.filter(x->
				x.getExperiment().equals(es))
				.collect(Collectors.toList());

		return !matchingOccurrencesForTheCurrentFile.isEmpty();
	}

	public ExperimentOutput getResult(Experiment arg0) {
		if(results == null)
			results = FileReadWriter.loadDataPoints(dataBaseLocation);
		
		Set<ExperimentOutput> res =
				results
				.stream()
				.filter(x->x.getExperiment().equals(arg0))
				.map(x->x.getExperimentOutput())
				.collect(Collectors.toSet());
		if(res.size()!=1)throw new Error();
		return res.iterator().next();
	}

	public void add(Experiment expe, ExperimentOutput out) {
		refreshCounter--;
		ExperimentExportFunctions.saveResultOnFile(expe, out, dataBaseLocation);
		if(results != null)
			results.add(DataPointImpl.newInstance(expe, out));
		if(refreshCounter==0) results = null;
	}

	public static FileBaseDatabase newInstance(String dbLocation) {
		return new FileBaseDatabase(new File(dbLocation).toPath());
	}

}

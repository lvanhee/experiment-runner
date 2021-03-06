package experimentrunner.inout.record;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import experimentrunner.inout.FileReadWriter;
import experimentrunner.inout.explore.ExperimentExportFunctions;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class FileBaseDatabase {
	
	private final Path dataBaseLocation;

	//results are updated from the file every few operations
	private int REFRESH_PERIOD = 5000;
	private int refreshCounter = REFRESH_PERIOD;
	
	private Set<DataPoint> results = null;

	
	private FileBaseDatabase(Path dataBaseLocation)
	{
		this.dataBaseLocation = dataBaseLocation;
		results = FileReadWriter.loadDataPoints(dataBaseLocation);
	}
	
	public synchronized boolean hasAlreadyBeenProcessed(
			ExperimentSetup es) {
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

	public synchronized ExperimentOutput getResult(ExperimentSetup arg0) {
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

	public synchronized void add(ExperimentSetup expe, ExperimentOutput out) {

		ExperimentExportFunctions.saveResultOnFile(expe, out, dataBaseLocation);
		results.add(DataPointImpl.newInstance(expe, out));
		System.out.println("Saving result:"+expe+","+out);

		refreshCounter--;
		if(refreshCounter==0)
		{
			refreshCounter = REFRESH_PERIOD;
			results = FileReadWriter.loadDataPoints(dataBaseLocation);
		}

		/*	if(results != null)
			results.add(DataPointImpl.newInstance(expe, out));
		if(refreshCounter==0) results = null;*/
	}

	public static FileBaseDatabase newInstance(String dbLocation) {
		return new FileBaseDatabase(new File(dbLocation).toPath());
	}

}

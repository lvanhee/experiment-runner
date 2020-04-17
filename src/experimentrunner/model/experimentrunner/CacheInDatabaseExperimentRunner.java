package experimentrunner.model.experimentrunner;

import java.io.File;
import java.util.function.Function;

import experimentrunner.inout.record.FileBaseDatabase;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class CacheInDatabaseExperimentRunner implements ExperimentRunner {
	private final FileBaseDatabase dataBase;
	private final ExperimentRunner runner;
	
	
	private CacheInDatabaseExperimentRunner(
			FileBaseDatabase dataBase,
			ExperimentRunner runner)
	{
		this.dataBase = dataBase;
		this.runner = runner;
	}
	
	@Override
	public ExperimentOutput apply(ExperimentSetup arg0) {
		if(!dataBase.hasAlreadyBeenProcessed(arg0))
			dataBase.add(arg0,runner.apply(arg0));
		return dataBase.getResult(arg0);
	}

	public static CacheInDatabaseExperimentRunner newInstance(
			String dbLocation,
			ExperimentRunner experimentComputer) {
		return new CacheInDatabaseExperimentRunner(
				FileBaseDatabase.newInstance(dbLocation),
				experimentComputer);
	}
}

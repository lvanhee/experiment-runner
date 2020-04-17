package experimentrunner.inout;

import java.nio.file.Path;

import experimentrunner.inout.FileReadWriter.FileFormat;
import experimentrunner.inout.explore.SearchOutput;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class PrintToFileSearchOutput implements SearchOutput {
	private final FileFormat ot;
	private final Path path;
	private final boolean saveEveryImprovement;
	
	public PrintToFileSearchOutput(FileFormat ot, Path path, boolean saveEveryImprovement) {
		this.ot = ot;
		this.path = path;
		this.saveEveryImprovement = saveEveryImprovement;
		
	}

	public static PrintToFileSearchOutput newInstance(FileFormat ot, Path outputFolder, boolean saveEveryImprovement) {
		return new PrintToFileSearchOutput(ot, outputFolder, saveEveryImprovement);
	}

	@Override
	public boolean updatesOnAnyImprovement() {
		return saveEveryImprovement;
	}

	@Override
	public void update(ExperimentSetup best, ExperimentOutput experimentOutput) {
		FileReadWriter.saveAs(DataPointImpl.newInstance(best, experimentOutput), 
				path, ot);
	}

}

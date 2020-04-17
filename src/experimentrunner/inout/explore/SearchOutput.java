package experimentrunner.inout.explore;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;

import experimentrunner.inout.FileReadWriter;
import experimentrunner.inout.FileReadWriter.FileFormat;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;

public interface SearchOutput {

	boolean updatesOnAnyImprovement();

	void update(ExperimentSetup best, ExperimentOutput experimentOutput);

	static SearchOutput parse(JSONObject object) {
		FileFormat ot  = FileFormat.parse((String)object.get("type"));
		
		Path outputFolder = null;

		outputFolder = Paths.get((String)object.get("file"));
		
		boolean saveEveryImprovement = object.get("output-modificators").equals("save-all-improvements");
		
		return PrintToFileSearchOutput.newInstance(ot, outputFolder,saveEveryImprovement);
	}

}

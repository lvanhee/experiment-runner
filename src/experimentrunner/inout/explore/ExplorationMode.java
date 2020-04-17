package experimentrunner.inout.explore;

import org.json.simple.JSONObject;

import experimentrunner.inout.HillClimberExplorationMode;
import experimentrunner.model.experiment.data.ExperimentSetup;

public interface ExplorationMode {

	public ExperimentSetup nextSetup(ExplorationHistory eh);
	public static ExplorationMode parse(Object object) {
		if(object instanceof JSONObject)
		{
			JSONObject input = ((JSONObject)object);
			String mode = (String)input.get("mode");
			if(mode.equals("hillclimbing"))
			{
				return HillClimberExplorationMode.parse(input);
			}
		}
		throw new Error();
	}

}

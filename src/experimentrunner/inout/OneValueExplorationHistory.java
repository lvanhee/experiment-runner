package experimentrunner.inout;

import experimentrunner.inout.explore.ExplorationHistory;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class OneValueExplorationHistory implements ExplorationHistory{
	private final ExperimentSetup s;
	private OneValueExplorationHistory(ExperimentSetup s)
	{
		this.s = s;
	}

	public static ExplorationHistory newInstance(ExperimentSetup best) {
		return new OneValueExplorationHistory(best);
	}

	public ExperimentSetup getLast() {
		return s;
	}

}

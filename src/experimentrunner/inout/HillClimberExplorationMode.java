package experimentrunner.inout;

import org.json.simple.JSONObject;

import experimentrunner.inout.explore.ExplorationHistory;
import experimentrunner.inout.explore.ExplorationMode;
import experimentrunner.inout.explore.SearchApproach;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class HillClimberExplorationMode implements ExplorationMode{
	
	private final SearchApproach sa;
	private final ValueSelection vs;

	private HillClimberExplorationMode(SearchApproach sa, ValueSelection vs) {
		this.sa = sa;
		this.vs = vs;
	}

	@Override
	public ExperimentSetup nextSetup(ExplorationHistory eh) {
		throw new Error();
	}

	public static ExplorationMode parse(JSONObject input) {
		SearchApproach sa = SearchApproach.parse(input.get("search-approach"));
		ValueSelection vs = ValueSelection.parse(input.get("value-selection"));
		return new HillClimberExplorationMode(sa,vs);
		
	}

	public SearchApproach getSearchApproach() {
		return sa;
	}
	
	public ValueSelection getValueSelection()
	{
		return vs;
	}

}

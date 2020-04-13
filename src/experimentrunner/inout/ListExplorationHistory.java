package experimentrunner.inout;

import java.util.Collection;
import java.util.List;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.ExperimentSetup;

public class ListExplorationHistory implements ExplorationHistory{
	
	private final List<DataPoint> points;
	
	private ListExplorationHistory(List<DataPoint>points)
	{
		this.points = points;
	}

	public List<DataPoint> getPoints() {
		return points;
	}

	public static ExplorationHistory newInstance(List<DataPoint> l) {
		return new ListExplorationHistory(l);
	}

	public DataPoint getLast() {
		return getPoints().get(getPoints().size()-1);
	}

}

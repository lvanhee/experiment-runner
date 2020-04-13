package experimentrunner.inout;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;


import experimentrunner.inout.ExplorationHistory.SimpleExplorationHistory;
import experimentrunner.inout.FileReadWriter.FileFormat;
import experimentrunner.inout.SearchApproach.SimpleSearchApproach;
import experimentrunner.inout.ValueSelection.SimpleValueSelection;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.values.DoubleValue;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ProcessingUtils;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class Search {

	static void performSearch(
			SearchOutput so, 
			MaximizeVariableExperimentGoal eg,
			JSONObject object,
			ExperimentVariableNetwork network,
			MultiExperimentExecutor mee) {
		ExplorationHistory eh =SimpleExplorationHistory.NONE;
		
		while(hasMoreExplorationToPerform(eh, eg))
		{
			eh = performExploration(
					eh,
					eg,
					network,
					mee,
					so);
		}
	}

	private static boolean hasMoreExplorationToPerform(ExplorationHistory eh,
			MaximizeVariableExperimentGoal eg			
			) {
		if(eh==SimpleExplorationHistory.NONE)return true;
		if(eg.getExplorationMode() instanceof HillClimberExplorationMode)
		{
			ListExplorationHistory prev = (ListExplorationHistory)eh;
			if(prev.getPoints().size()<2) return true;
			DataPoint last = prev.getPoints().get(prev.getPoints().size()-1);
			DataPoint last2 = prev.getPoints().get(prev.getPoints().size()-2);
			boolean res = !(last.getValueOf(eg.getVariableToOptimizeAgainst()).equals(
					last2.getValueOf(eg.getVariableToOptimizeAgainst())));
			return res;
		}
		throw new Error();
	}

	private static ExplorationHistory performExploration(
			ExplorationHistory eh, 
			MaximizeVariableExperimentGoal eg,
			ExperimentVariableNetwork network,
			MultiExperimentExecutor mee, SearchOutput so
			) {
		Set<ExperimentSetup> toExplore = new HashSet<>(); 
		if(eh==SimpleExplorationHistory.NONE)
		{
			toExplore.add(network.getRandomExperiment());
		}
		else if(eh instanceof ListExplorationHistory)
		{
			ExperimentSetup bestSoFar = ((ListExplorationHistory)eh).getLast().getExperiment();
			if(eg.getExplorationMode() instanceof HillClimberExplorationMode)
			{
				HillClimberExplorationMode em = ((HillClimberExplorationMode)eg.getExplorationMode());
				toExplore.addAll(getToExploreHillClimber(em, network,bestSoFar));
			}
			else throw new Error();
		}else throw new Error();

		System.out.println("Exploring "+toExplore.size()+": "+toExplore);
		
		Map<ExperimentSetup, ExperimentOutput> results = 
				mee.apply(toExplore);

		return getUpdatedHistory(eh, results, so,eg);
		
	}

	private static Set<ExperimentSetup> getToExploreHillClimber(HillClimberExplorationMode em, 
			ExperimentVariableNetwork network,
			ExperimentSetup bestSoFar) {
		Set<ExperimentSetup> res = new HashSet<ExperimentSetup>();
		if(em.getSearchApproach()==SimpleSearchApproach.ONE_VARIABLE_AT_A_TIME
				&&em.getValueSelection()==SimpleValueSelection.ALL_VALUES)
		{
			for(Variable v:network.getInputVariables())
			{
				res.addAll(ProcessingUtils.getVariantsAlongVariable(
						bestSoFar,
						v, network.getRangeOf(v)
						));
			}
		}
		return res;
	}

	private static ExplorationHistory getUpdatedHistory(ExplorationHistory eh,
			Map<ExperimentSetup, ExperimentOutput>results, SearchOutput so, 
			MaximizeVariableExperimentGoal eg) {
		if(eg.getExplorationMode() instanceof HillClimberExplorationMode);
		{
			ExperimentSetup best = 
					results.keySet().stream()
					.max((x,y)->
					Double.compare(
							((DoubleValue)results.get(x).getResultMap().get(eg.getVariableToOptimizeAgainst())).getValue(),
							((DoubleValue)results.get(y).getResultMap().get(eg.getVariableToOptimizeAgainst())).getValue())
							).get();
			
			List<DataPoint> l = new ArrayList<DataPoint>();
			if(eh instanceof ListExplorationHistory)
				l.addAll(((ListExplorationHistory)eh).getPoints());
			l.add(DataPointImpl.newInstance(best,results.get(best)));
			
			if(so.updatesOnAnyImprovement())
			{
				so.update(best, results.get(best));
				System.out.println("New optimal found:"+best+":"+results.get(best));
			}
			return ListExplorationHistory.newInstance(l);
		}
	}
}

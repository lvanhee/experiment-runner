package experimentrunner.mains;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentexecutor.ExperimentBatchRunner;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentexecutor.ExperimentBatchRunner.Randomize;
import experimentrunner.model.experimentrunner.CacheInDatabaseExperimentRunner;
import experimentrunner.model.experimentrunner.ExperimentRunner;
import experimentrunner.modules.netlogo.NetLogoExperimentsRunner;

public class MainNetLogoDiffExplorer {
	private static final String MODEL_LOCATION = "/media/vanhee/e3672a0d-4cc9-4c8f-95b0-0e8d5e9368e2/Dropbox/Travail/Recherche/Ecrits/Papiers/2019 SCS-CVS/experiment/experiments.nlogo";
	private static final String TERMINATION_COMMAND = "not any? robots";
	private static final String DB_LOCATION = "./output/db.txt"; 
	
	
	/**
	 * -Dnetlogo.models.dir=/export/home/vanhee/Bureau/NetLogo/app/extensions
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		ExperimentRunner experimentComputer =
				NetLogoExperimentsRunner.newInstance(
						MODEL_LOCATION,
						Arrays.asList("ticks", "time-all-deliveries-completed"),
						Optional.of(TERMINATION_COMMAND)
						);

		CacheInDatabaseExperimentRunner runner = 
				CacheInDatabaseExperimentRunner.newInstance(DB_LOCATION, experimentComputer);
		
		Function<ExperimentLinearScheduler, Double> criteriaToMaximize =
				(e->
				{
					Set<ExperimentSetup> uniqueCVSExperiments = 
							e.getAllSetups()
							.stream()
							.filter(x->
							Integer.parseInt(
									x.getVariableAllocation().get("#obedient-robots").toString())==0
							|| Integer.parseInt(
									x.getVariableAllocation().get("#obedient-robots").toString())==4)
							.collect(Collectors.toSet());
					Set<ExperimentSetup> multiCVSExperiments = 
							e.getAllSetups()
							.stream()
							.filter(x->!uniqueCVSExperiments.contains(x))
							.collect(Collectors.toSet());
					

					double totalAllDelivUnique = 
							uniqueCVSExperiments
							.stream()
							.map(x->
							Double.parseDouble(
									runner
									.apply(x)
									.getResultMap().get("time-all-deliveries-completed")))
							.reduce
							(0d, (Double x,Double y)->x+y)/uniqueCVSExperiments.size();
					double totalAllDelivMulti = 
							multiCVSExperiments
							.stream()
							.map(x->
							Double.parseDouble(
									runner
									.apply(x)
									.getResultMap().get("time-all-deliveries-completed")))
							.reduce
							(0d, (Double x,Double y)->x+y)/multiCVSExperiments.size();
					double shiftFactor = (totalAllDelivMulti/totalAllDelivUnique);


					double totalAllTicksUnique = 
							uniqueCVSExperiments
							.stream()
							.map(x->
							{
								ExperimentOutput out = runner
										.apply(x);
								
								String val = out
										.getResultMap().get("ticks");
								return Double.parseDouble(
										val);
							})
							.reduce
							(0d, (Double x,Double y)->x+y)/uniqueCVSExperiments.size();
					double totalAllticksMulti = 
							multiCVSExperiments
							.stream()
							.map(x->
							Double.parseDouble(
									runner
									.apply(x)
									.getResultMap().get("ticks")))
							.reduce
							(0d, (Double x,Double y)->x+y)/multiCVSExperiments.size();

					double shiftFactorTicks = (totalAllticksMulti/totalAllTicksUnique);

					return Double.max(shiftFactor, shiftFactorTicks);
				});
		
		
		Set<Variable> definableVariables = new HashSet<>();
		definableVariables.add(VariableImpl.newInstance("#deliveries", 
				Arrays.asList("4","5","6","8","9","12","15","18")));
		definableVariables.add(VariableImpl.newInstance("robot-carry-capacity", 1,1,2));
		definableVariables.add(VariableImpl.newInstance("grid-width", 7,4,15));
		definableVariables.add(VariableImpl.newInstance("grid-height", 7,4,15));		
		
		Set<Variable> comparisonVariables = new HashSet<>();
		comparisonVariables.add(VariableImpl.newInstance("r-seed", 1, 1, 2));
		comparisonVariables.add(VariableImpl.newInstance("#obedient-robots", 0, 1, 4));
		comparisonVariables.add(VariableImpl.newInstance("#fallen-crates", 0, 5, 20));
		
		Predicate<ExperimentSetup> validityChecker = 
				(x-> {
					if(
							Integer.parseInt(x.getVariableAllocation().get("grid-width").toString())+
							Integer.parseInt(x.getVariableAllocation().get("grid-height").toString())
							<=14
							&&
							Integer.parseInt(x.getVariableAllocation().get("#deliveries").toString())>=18)
						return false;
					return true;
							
				});

		ExperimentLinearScheduler res = ExperimentBatchRunner
				.getSeriesMaximizing(
						definableVariables,
						comparisonVariables, 
						criteriaToMaximize, 
						runner,
						Randomize.SHUFFLE,
						validityChecker
						); 
	}
}

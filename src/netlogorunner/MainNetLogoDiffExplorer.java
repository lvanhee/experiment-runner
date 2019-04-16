package netlogorunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import experiments.model.Experiment;
import experiments.model.ExperimentOutput;
import experiments.model.ExperimentSeries;
import experiments.model.Variable;
import experiments.model.experimentRunner.CacheInDatabaseExperimentRunner;
import experiments.model.experimentRunner.ExperimentRunner;
import experiments.processing.ExperimentBatchRunner;
import experiments.processing.ExperimentBatchRunner.Randomize;

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
		
		Function<ExperimentSeries, Double> criteriaToMaximize =
				(e->
				{
					Set<Experiment> uniqueCVSExperiments = 
							e.getSetups()
							.stream()
							.filter(x->
							Integer.parseInt(x.getInputMap().get("#obedient-robots"))==0
							|| Integer.parseInt(x.getInputMap().get("#obedient-robots"))==4)
							.collect(Collectors.toSet());
					Set<Experiment> multiCVSExperiments = 
							e.getSetups()
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
		definableVariables.add(Variable.newInstance("#deliveries", 
				Arrays.asList("4","5","6","8","9","12","15","18")));
		definableVariables.add(Variable.newInstance("robot-carry-capacity", 1,1,2));
		definableVariables.add(Variable.newInstance("grid-width", 7,4,15));
		definableVariables.add(Variable.newInstance("grid-height", 7,4,15));		
		
		Set<Variable> comparisonVariables = new HashSet<>();
		comparisonVariables.add(Variable.newInstance("r-seed", 1, 1, 2));
		comparisonVariables.add(Variable.newInstance("#obedient-robots", 0, 1, 4));
		comparisonVariables.add(Variable.newInstance("#fallen-crates", 0, 5, 20));
		
		Predicate<Experiment> validityChecker = 
				(x-> {
					if(
							Integer.parseInt(x.getInputMap().get("grid-width"))+
							Integer.parseInt(x.getInputMap().get("grid-height"))
							<=14
							&&
							Integer.parseInt(x.getInputMap().get("#deliveries"))>=18)
						return false;
					return true;
							
				});

		ExperimentSeries res = ExperimentBatchRunner
				.getSeriesMaximizing(
						definableVariables,
						comparisonVariables, 
						criteriaToMaximize, 
						runner,
						Randomize.NO,
						validityChecker
						); 
	}
}

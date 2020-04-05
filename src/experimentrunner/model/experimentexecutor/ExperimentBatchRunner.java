package experimentrunner.model.experimentexecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import experimentrunner.inout.ResultPlotter;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class ExperimentBatchRunner {
	
	public enum Randomize {NO, SHUFFLE;} 
	

	
	public static boolean hasAlreadyBeenProcessed(
			ExperimentSetup es, 
			Path outputFilePath) {
		try {
			if(!Files.exists(outputFilePath))return false;
			if(Files.exists(outputFilePath)) 
			{
				List<String> matchingOccurrencesForTheCurrentFile = 
						Files.readAllLines(outputFilePath)
						.stream()
						.filter(x->
						x.startsWith(
								es.getVariableAllocation().toString()))
						.collect(Collectors.toList());

				return !matchingOccurrencesForTheCurrentFile.isEmpty();
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
		throw new Error();
	}

	public static boolean isSimplerInstanceRejected(
			ExperimentSetup x,
			Set<ExperimentSetup> rejectedInstances,
			BiPredicate<ExperimentSetup, ExperimentSetup> isSimpler) {
		return rejectedInstances
				.stream()
				.anyMatch(y->isSimpler.test(y,x));			
	}



private static final boolean PLOT = false;
	public static ExperimentLinearScheduler getSeriesMaximizing(
			Collection<ExperimentLinearScheduler> series,
			Function<ExperimentLinearScheduler, Double> criteriaToMaximize,
			Optional<ExperimentLinearScheduler> optionalBest,
			ExperimentRunner er
			) {

		Set<ExperimentSetup> experimentsToBeRun = 
				series.stream().map(x->x.getAllSetups()).reduce(
						new LinkedList<ExperimentSetup>(), (x,y)->{x.addAll(y); return x;})
				.stream().collect(Collectors.toSet());
		
		System.out.println("Number of comparions to run:"+series.stream().collect(Collectors.toSet()).size());
		System.out.println("Number of experiments to run:"+experimentsToBeRun.stream().collect(Collectors.toSet()).size());
		
		AtomicInteger nbRun = new AtomicInteger(1);

		ExperimentLinearScheduler bestSoFar = null;
		double bestVal = Double.MIN_VALUE;

		if(optionalBest.isPresent()) {bestSoFar = optionalBest.get();bestVal = criteriaToMaximize.apply(bestSoFar);}
		for(ExperimentLinearScheduler challenger : series)
		{
			if(bestSoFar==null)
				{
				bestSoFar = challenger;
				bestVal = criteriaToMaximize.apply(bestSoFar);
				if (PLOT)
					ResultPlotter.plot(
							DataPointImpl.from(bestSoFar, er), 
							"#obedient-robots","#fallen-crates",
							"time-all-deliveries-completed",
							bestVal+":"+bestSoFar.getLockedVariablesMap()
							);

				}

			System.out.println(nbRun.incrementAndGet()+"/"+series.size());
			
			double challengerVal = criteriaToMaximize.apply(challenger);
			System.out.println("Comparing:\n"+challenger.getLockedVariablesMap()+":\n"+challengerVal+" and\n"+bestSoFar.getLockedVariablesMap()+":\n"+bestVal);	

			if(challengerVal > bestVal)
			{
				System.out.println("New best found:"+challenger+":"+criteriaToMaximize.apply(challenger));
				System.out.println("Better than:"+bestSoFar+":"+criteriaToMaximize.apply(bestSoFar));
				bestSoFar = challenger;
				bestVal = challengerVal;

				if (PLOT)
					ResultPlotter.plot(
							DataPointImpl.from(bestSoFar, er), 
							"#obedient-robots","#fallen-crates", 
							"time-all-deliveries-completed",
							bestVal+":"+bestSoFar.getLockedVariablesMap()
							);

				while(true)
				{
					Set<ExperimentLinearScheduler> neighbors = getNeighborsOf(bestSoFar, series);
					ExperimentLinearScheduler bestInSurroundingsOfBest =  getSeriesMaximizing(neighbors, criteriaToMaximize, Optional.of(bestSoFar),er);
					double localBestVal = criteriaToMaximize.apply(bestInSurroundingsOfBest);
					if(bestVal>=localBestVal)
						break;
					else 
					{
						bestVal = localBestVal;
						if (PLOT)
							ResultPlotter.plot(
									DataPointImpl.from(bestSoFar, er),
									"#obedient-robots","#fallen-crates",
									"time-all-deliveries-completed",
									bestVal+":"+bestSoFar.getLockedVariablesMap()
									);
					}

				}
			}
		}
		return bestSoFar;
	}

	private static Set<ExperimentLinearScheduler> getNeighborsOf(ExperimentLinearScheduler max,
			Collection<ExperimentLinearScheduler> series) {

		Set<ExperimentLinearScheduler>res = new HashSet<>();
		for(ExperimentLinearScheduler es: series)
		{
			for(ExperimentSetup exp: es.getAllSetups())
			{
				for(ExperimentSetup expToMatch : max.getAllSetups())
				{
					int shifts = 0;
					for(String input:exp.getVariableAllocation().keySet())
						if(!exp.getVariableAllocation().get(input).equals(expToMatch.getVariableAllocation().get(input)))
							shifts++;
					if(shifts <= 1) res.add(es);
					break;
				}
				if(res.contains(es))break;
			}
		}
		return res;
	}

	
	public static double getAverageDivergence(
			ExperimentLinearScheduler es, 
			String parameter,
			String value,
			String outputParameter,
			ExperimentRunner nler) {
		
		double res = 0;
		for(ExperimentSetup setup: es.getAllSetups())
		{
			ExperimentOutput out = nler.apply(setup);
			
			ExperimentSetup tilted = ExperimentSetup.getVariantReplacingTheValueOfAVariableBy(setup,parameter,value);
			ExperimentOutput outTilted = nler.apply(tilted);
			
			double normalResult = Double.parseDouble(out.getResultMap().get(outputParameter));
			double tiltedResult = Double.parseDouble(outTilted.getResultMap().get(outputParameter));

			res+= Math.abs(normalResult-tiltedResult);
		}
		
		res/=es.getAllSetups().size();
		return res;
	}
	
	

	public static ExperimentLinearScheduler getSeriesMaximizing(Set<Variable> definableVariables,
			Set<Variable> comparisonVariables,
			Function<ExperimentLinearScheduler, Double> criteriaToMaximize, 
			ExperimentRunner er,
			Randomize r, Predicate<ExperimentSetup> validityChecker
			) {
		List<ExperimentLinearScheduler> expe = ExperimentLinearScheduler.cartesianProduct(
				ExperimentLinearScheduler.newInstance(definableVariables),
				ExperimentLinearScheduler.newInstance(comparisonVariables)); 
		
		expe = 
				expe.stream().filter(x->
				!x.getAllSetups().stream().anyMatch(y->
				!validityChecker.test(y)))
				.collect(Collectors.toList());
		
		if(r.equals(Randomize.SHUFFLE))
			Collections.shuffle(expe);
		
		
		return getSeriesMaximizing(
				expe,
				criteriaToMaximize, Optional.empty(),er);
	}

	public static Set<DataPoint> process(ExperimentLinearScheduler es, ExperimentRunner er) {
		return es.getAllSetups().parallelStream().map(x->DataPointImpl.newInstance(x, er.apply(x)))
				.collect(Collectors.toSet());
	}
}

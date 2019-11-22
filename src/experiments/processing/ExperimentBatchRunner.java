package experiments.processing;

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

import experiments.inout.plotting.ResultPlotter;
import experiments.model.DataPoint;
import experiments.model.DataPointImpl;
import experiments.model.Experiment;
import experiments.model.ExperimentOutput;
import experiments.model.ExperimentSeries;
import experiments.model.Variable;
import experiments.model.VariableImpl;
import experiments.model.experimentRunner.ExperimentRunner;

public class ExperimentBatchRunner {
	
	public enum Randomize {NO, SHUFFLE;} 
	

	
	public static boolean hasAlreadyBeenProcessed(
			Experiment es, 
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
								es.getInputMap().toString()))
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
			Experiment x,
			Set<Experiment> rejectedInstances,
			BiPredicate<Experiment, Experiment> isSimpler) {
		return rejectedInstances
				.stream()
				.anyMatch(y->isSimpler.test(y,x));			
	}



private static final boolean PLOT = false;
	public static ExperimentSeries getSeriesMaximizing(
			Collection<ExperimentSeries> series,
			Function<ExperimentSeries, Double> criteriaToMaximize,
			Optional<ExperimentSeries> optionalBest,
			ExperimentRunner er
			) {

		Set<Experiment> experimentsToBeRun = 
				series.stream().map(x->x.getSetups()).reduce(
						new LinkedList<Experiment>(), (x,y)->{x.addAll(y); return x;})
				.stream().collect(Collectors.toSet());
		
		System.out.println("Number of comparions to run:"+series.stream().collect(Collectors.toSet()).size());
		System.out.println("Number of experiments to run:"+experimentsToBeRun.stream().collect(Collectors.toSet()).size());
		
		AtomicInteger nbRun = new AtomicInteger(1);

		ExperimentSeries bestSoFar = null;
		double bestVal = Double.MIN_VALUE;

		if(optionalBest.isPresent()) {bestSoFar = optionalBest.get();bestVal = criteriaToMaximize.apply(bestSoFar);}
		for(ExperimentSeries challenger : series)
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
					Set<ExperimentSeries> neighbors = getNeighborsOf(bestSoFar, series);
					ExperimentSeries bestInSurroundingsOfBest =  getSeriesMaximizing(neighbors, criteriaToMaximize, Optional.of(bestSoFar),er);
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

	private static Set<ExperimentSeries> getNeighborsOf(ExperimentSeries max,
			Collection<ExperimentSeries> series) {

		Set<ExperimentSeries>res = new HashSet<>();
		for(ExperimentSeries es: series)
		{
			for(Experiment exp: es.getSetups())
			{
				for(Experiment expToMatch : max.getSetups())
				{
					int shifts = 0;
					for(String input:exp.getInputMap().keySet())
						if(!exp.getInputMap().get(input).equals(expToMatch.getInputMap().get(input)))
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
			ExperimentSeries es, 
			String parameter,
			String value,
			String outputParameter,
			ExperimentRunner nler) {
		
		double res = 0;
		for(Experiment setup: es.getSetups())
		{
			ExperimentOutput out = nler.apply(setup);
			
			Experiment tilted = Experiment.getTiltedVariant(setup,parameter,value);
			ExperimentOutput outTilted = nler.apply(tilted);
			
			double normalResult = Double.parseDouble(out.getResultMap().get(outputParameter));
			double tiltedResult = Double.parseDouble(outTilted.getResultMap().get(outputParameter));

			res+= Math.abs(normalResult-tiltedResult);
		}
		
		res/=es.getSetups().size();
		return res;
	}
	
	

	public static ExperimentSeries getSeriesMaximizing(Set<Variable> definableVariables,
			Set<Variable> comparisonVariables,
			Function<ExperimentSeries, Double> criteriaToMaximize, 
			ExperimentRunner er,
			Randomize r, Predicate<Experiment> validityChecker
			) {
		List<ExperimentSeries> expe = ExperimentSeries.cartesianProduct(
				ExperimentSeries.newInstance(definableVariables),
				ExperimentSeries.newInstance(comparisonVariables)); 
		
		expe = 
				expe.stream().filter(x->
				!x.getSetups().stream().anyMatch(y->
				!validityChecker.test(y)))
				.collect(Collectors.toList());
		
		if(r.equals(Randomize.SHUFFLE))
			Collections.shuffle(expe);
		
		
		return getSeriesMaximizing(
				expe,
				criteriaToMaximize, Optional.empty(),er);
	}

	public static Set<DataPoint> process(ExperimentSeries es, ExperimentRunner er) {
		return es.getSetups().parallelStream().map(x->DataPointImpl.newInstance(x, er.apply(x)))
				.collect(Collectors.toSet());
	}
}

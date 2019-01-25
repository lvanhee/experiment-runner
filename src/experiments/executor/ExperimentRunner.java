package experiments.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import experiments.executor.timeout.TimeoutParameters;
import experiments.model.Experiment;
import experiments.model.ExperimentOutput;

public class ExperimentRunner {

	public static void 
	runExperiments(
			List<Experiment> experiments,
			Function<Experiment, ExperimentOutput> executor,
			BiConsumer<Experiment, ExperimentOutput> saver,
			Predicate<Experiment> shouldRunInstance,
			TimeoutParameters tp
			)
	{
		//Map<Experiment, ExperimentOutput>res = new HashMap<Experiment, ExperimentOutput>();
		for(Experiment e: experiments)
			if(shouldRunInstance.test(e))
			{
				AtomicBoolean ab = new AtomicBoolean(false);
				Thread runThread = new Thread(()->saver.accept(e, executor.apply(e)));
				Thread timeoutThread = new Thread(()-> {
					try {
						Thread.sleep(tp.getMillis());
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} if(!ab.get()) { tp.getReactionOnTimeout().accept(e); runThread.stop();}});
				timeoutThread.start();
				runThread.run();	
				ab.set(true);
			}
	}
	
	

	
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
}

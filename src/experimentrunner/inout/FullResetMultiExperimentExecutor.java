package experimentrunner.inout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class FullResetMultiExperimentExecutor implements MultiExperimentExecutor{

	private final Supplier<ExperimentRunner>er;
	private final boolean verbose;
	private final boolean parallelOps;
	ConcurrentLinkedQueue<ExperimentRunner> runners = new ConcurrentLinkedQueue<ExperimentRunner>();
	public FullResetMultiExperimentExecutor(Supplier<ExperimentRunner> er,boolean verbose, boolean parallelOps) {
		this.er = er;
		this.verbose = verbose;
		this.parallelOps = parallelOps;
	}

	@Override
	public Map<ExperimentSetup, ExperimentOutput> apply(Set<ExperimentSetup> t) {
		if(parallelOps)
			return
				t.parallelStream()
				.collect(Collectors.toMap(Function.identity(), x->apply(x)));
		else
			return
				t.stream()
				.collect(Collectors.toMap(Function.identity(), x->apply(x)));
	}

	public static MultiExperimentExecutor newInstance(Supplier<ExperimentRunner> er, 
			boolean verbose, boolean parallelOps) {
		return new FullResetMultiExperimentExecutor(er, verbose,parallelOps);
	}

	private final Map<ExperimentSetup, ExperimentOutput>cache 
	= new HashMap<ExperimentSetup, ExperimentOutput>();
	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		if(cache.containsKey(t))
		{
			if(verbose)
				System.out.println(t+":"+cache.get(t));
			return cache.get(t);
		}
		ExperimentRunner runner = null;
		//	synchronized (runners) {
		while(runner==null)
		{
			if(runners.isEmpty())
			{
				runners.add(er.get());
				//		System.out.println("Created runner");
			}
			runner = runners.poll();
		}

		ExperimentOutput res =runner.apply(t); 
		if(verbose)
			System.out.println(t+":"+res);
		//synchronized (runners) {
		runners.add(runner);
		//	System.out.println("Releasing a runner:"+runners.size());
		//}
		cache.put(t,res);
		return res;
	}

}
